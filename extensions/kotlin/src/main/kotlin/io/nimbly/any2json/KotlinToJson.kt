package io.nimbly.any2json

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.incremental.components.LookupLocation
import org.jetbrains.kotlin.js.descriptorUtils.nameIfStandardType
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.nj2k.postProcessing.type
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.resolve.calls.callUtil.createLookupLocation
import org.jetbrains.kotlin.types.DeferredType
import org.jetbrains.kotlin.types.FlexibleType
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.SimpleType
import org.jetbrains.kotlin.types.typeUtil.isEnum
import org.jetbrains.kotlin.types.typeUtil.supertypes

class KotlinToJson : Any2JsonExtensionPoint<String> {

    @Suppress("UNCHECKED_CAST")
    override fun build(event: AnActionEvent, actionType: EType) : Pair<String, Map<String, Any>>? {

        if (!isEnabled(event, actionType))
            return null

        val editor = event.getData(CommonDataKeys.EDITOR) ?: return null
        val psiFile = event.getData(CommonDataKeys.PSI_FILE) ?: return null
        val element = psiFile.findElementAt(editor.caretModel.offset)

        val type = PsiTreeUtil.getContextOfType(element, KtClass::class.java)
            ?: return null

        return type.name!! to type.getProperties()
            .map { it.name to parse(
                it.type(),
                it.initializer?.text,
                type.createLookupLocation()!!,
                actionType)}
            .filter { it.second != null }
            .toMap() as Map<String, Any>
    }

    @Suppress("NAME_SHADOWING")
    private fun parse(
        type: KotlinType?,
        initializer: String?,
        lookupLocation: LookupLocation,
        actionType: EType,
        done: MutableSet<KotlinType> = mutableSetOf()
    ): Any? {

        val initializer = if (initializer == "null") null else initializer

        var type = type
        if (type == null)
            return null

        if (type is DeferredType)
            type = type.unwrap()

        if (type is FlexibleType)
            type = type.delegate

        // Enum
        if (type is SimpleType && type.isEnum()) {
            val names = mutableListOf<Name>()
            type.memberScope.getClassifierNames()?.let { names.addAll(it) }
            type.memberScope.getVariableNames().let { names.addAll(it) }
            return names
                .map { it.identifier }
                .firstOrNull { it != "name" && it != "ordinal" && it != "Companion" }
        }

        // Known object with generator
        val typeName = type.nameIfStandardType?.identifier ?:
        type.toString().substringBeforeLast("?")
        GENERATORS[typeName]?.let {
            return it.generate(actionType == EType.SECONDARY, initializer)
        }

        // Collections, iterables, arrays, etc.
        val names = mutableListOf<String>()
        names += typeName.substringAfterLast(".")
        names += type.supertypes()
            .map { it.nameIfStandardType?.identifier ?: it.toString() }
            .map { it.substringAfterLast(".")}
        if (names.find { it.startsWith("Collection")
                    || it.startsWith("Array")
                    || it.startsWith("Iterable")
                    || it.startsWith("Iterator")
                    || it.startsWith("List") } != null) {
            val parameterType = type.arguments.first().type
            if (parameterType.toString().substringBeforeLast("?") == "Any")
                return listOf<Int>()
            return listOfNotNull(parse(parameterType, null, lookupLocation, actionType, done = done))
        }

        // Avoid stack overflow
        if (done.contains(type))
            return null
        done.add(type)

        // Recurse
        return type.memberScope.getVariableNames().map {
            it.identifier to parse(
                type.memberScope.getContributedVariables(it, lookupLocation).firstOrNull()?.returnType,
                type.memberScope.getContributedVariables(it, lookupLocation).firstOrNull()?.compileTimeInitializer?.value.toString(),
                lookupLocation, actionType, done)
        }.toMap()
    }

    override fun isEnabled(event: AnActionEvent, actionType: EType): Boolean {
        val psiFile : PsiFile = event.getData(CommonDataKeys.PSI_FILE) ?: return false
        return psiFile.name.endsWith(".kt")
    }

    override fun presentation(actionType: EType)
            = "from Class" + if (actionType == EType.SECONDARY) " with Data" else ""

    companion object {
        val GENERATORS = mapOf(
            "Boolean" to GBoolean(),
            "Int" to GInteger(), "Long" to GLong(),
            "Number" to GInteger(),
            "Char" to GChar(),
            "CharSequence" to GString(), "String" to GString(),
            "Double" to GDecimal(1), "Float" to GDecimal(6), "BigDecimal" to GDecimal(12),
            "Date" to GDateTime(), "LocalDateTime" to GDateTime(),
            "LocalDate" to GDate(),
            "LocalTime" to GTime(),
        )
    }
}