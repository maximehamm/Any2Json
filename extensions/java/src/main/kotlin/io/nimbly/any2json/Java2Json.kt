package io.nimbly.any2json

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.psi.CommonClassNames
import com.intellij.psi.PsiArrayType
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiEnumConstant
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiModifier
import com.intellij.psi.PsiPrimitiveType
import com.intellij.psi.PsiType
import com.intellij.psi.impl.source.PsiClassReferenceType
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.PsiUtil
import io.nimbly.any2json.EType.SECONDARY

class Java2Json : Any2JsonExtensionPoint {

    @Suppress("UNCHECKED_CAST")
    override fun build(event: AnActionEvent, actionType: EType) : Pair<String, Map<String, Any>>? {

        if (!isEnabled(event, actionType))
            return null

        val editor = event.getData(CommonDataKeys.EDITOR) ?: return null
        val psiFile = event.getData(CommonDataKeys.PSI_FILE) ?: return null
        val element = psiFile.findElementAt(editor.caretModel.offset)

        val type = PsiTreeUtil.getContextOfType(element, PsiClass::class.java)
            ?: return null

        return type.name!! to type.allFields
            .filter { it.modifierList?.hasModifierProperty(PsiModifier.STATIC) == false || type.isInterface }
            .map { it.name to parse(it.type, it.initializer?.text, actionType) }
            .filter { it.second != null }
            .toMap() as Map<String, Any>
    }

    private fun parse(
        type: PsiType,
        initializer: String?,
        actionType: EType,
        done: MutableSet<PsiType> = mutableSetOf()
    ): Any? {

        // Primitives
        if (type is PsiPrimitiveType)
            return getValue(type, actionType == SECONDARY, initializer)

        // Primitive array
        if (type is PsiArrayType)
            return listOfNotNull(parse(type.getDeepComponentType(), initializer, actionType, done = done))

        // Resolve Psi class
        val psiClass = PsiUtil.resolveClassInClassTypeOnly(type)
            ?: (if (type is PsiClassReferenceType)
                type.resolve() else null)
            ?: return mapOf<String, Any?>()

        // Enum
        if (psiClass.isEnum)
            return psiClass.fields.find { it is PsiEnumConstant }?.name ?: ""

        // Known object with generator
        val names = mutableListOf<String>()
        names += type.presentableText
        names += type.superTypes.map { it.presentableText }
        names.find { GENERATORS[it] != null }?.let {
            return getValue(it, actionType == SECONDARY, initializer)
        }

        // Collections, iterables, arrays, etc.
        if (names.find { it.startsWith("Collection")
                    || it.startsWith("Array")
                    || it.startsWith("Iterable")
                    || it.startsWith("Iterator")
                    || it.startsWith("List") } != null) {
            val parameterType = PsiUtil.extractIterableTypeParameter(type, false)
                ?: PsiUtil.substituteTypeParameter(type, CommonClassNames.JAVA_UTIL_ITERATOR, 0, true)
                ?: PsiUtil.substituteTypeParameter(type, CommonClassNames.JAVA_UTIL_LIST, 0, true)
                ?: return null
            if (parameterType.presentableText == "Object")
                return listOf<Int>()
            return listOfNotNull(parse(parameterType, null, actionType, done = done))
        }

        // Prevent stack overflow
        if (done.contains(type))
            return null
        done.add(type)

        // Recurse
        return psiClass.allFields.map {
            it.name to parse(
                it.type,
                it.initializer?.text,
                actionType,
                done = done) }.toMap()
    }

    private fun getValue(type: String, generateValues: Boolean, initializer: String?)
        = GENERATORS[type]!!.generate(generateValues, initializer)

    private fun getValue(type: PsiType, generateValues: Boolean, initializer: String?)
        = when (type.canonicalText) {
            "boolean" -> getValue("Boolean", generateValues, initializer)
            "int", "long", "byte", "short" -> getValue("Number", generateValues, initializer)
            "float" -> getValue("Float", generateValues, initializer)
            "double" -> getValue("Double", generateValues, initializer)
            "char" -> getValue("Character", generateValues, initializer)
            else -> throw Any2PojoException("Not supported primitive '$type.canonicalText'")
        }

    override fun isEnabled(event: AnActionEvent, actionType: EType): Boolean {
        val psiFile : PsiFile = event.getData(CommonDataKeys.PSI_FILE) ?: return false
        return psiFile.name.endsWith(".java")
    }

    override fun presentation(actionType: EType)
            = "from Class" + if (actionType == SECONDARY) " with Data" else ""

    companion object {
        val GENERATORS = mapOf(
            "Boolean" to GBoolean(),
            "Character" to GChar(),
            "CharSequence" to GString(),
            "Long" to GLong(),
            "Number" to GLong(),
            "Double" to GDecimal(1), "Float" to GDecimal(6), "BigDecimal" to GDecimal(12),
            "Date" to GDateTime(), "LocalDateTime" to GDateTime(),
            "LocalDate" to GDate(),
            "LocalTime" to GTime()
        )
    }
}