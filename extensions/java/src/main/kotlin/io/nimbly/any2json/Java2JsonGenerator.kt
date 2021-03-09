package io.nimbly.any2json

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.psi.*
import com.intellij.psi.impl.source.PsiClassReferenceType
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.PsiUtil
import io.nimbly.any2json.EAction.COPY
import io.nimbly.any2json.EAction.PREVIEW
import io.nimbly.any2json.util.processAction

class Java2JsonGeneratePreview : AbstractJava2JsonGenerate(PREVIEW), Any2JsonPreviewExtensionPoint

class Java2JsonGenerateCopy : AbstractJava2JsonGenerate(COPY), Any2JsonCopyExtensionPoint

abstract class AbstractJava2JsonGenerate(private val action: EAction) : Any2JsonRootExtensionPoint {

    override fun process(event: AnActionEvent): Boolean {

        val editor = event.getData(CommonDataKeys.EDITOR) ?: return false
        val project = event.project ?: return false
        val psiFile = event.getData(CommonDataKeys.PSI_FILE) ?: return false
        val element = psiFile.findElementAt(editor.caretModel.offset)

        val type = PsiTreeUtil.getContextOfType(element, PsiClass::class.java) ?: return false

        val map = type.allFields
            .filter { it.modifierList?.hasModifierProperty(PsiModifier.STATIC) == false || type.isInterface }
            .map { it.name to parse(it.type, it.initializer?.text) }
            .filter { it.second != null }
            .toMap()

        val json = toJson(map)

        return processAction(action, json, project, event.dataContext)
    }

    private fun parse(
        type: PsiType,
        initializer: String?,
        actionType: EType = EType.SECONDARY,
        done: MutableSet<PsiType> = mutableSetOf()
    ): Any? {

        // Primitives
        if (type is PsiPrimitiveType)
            return getValue(type, actionType == EType.SECONDARY, initializer)

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
            return getValue(it, actionType == EType.SECONDARY, initializer)
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

    override fun isEnabled(event: AnActionEvent): Boolean {
        val psiFile = event.getData(CommonDataKeys.PSI_FILE)
            ?: return false
        if (!psiFile.name.endsWith(".java"))
            return false

        val editor = event.getData(CommonDataKeys.EDITOR) ?:
        return false

        val element = psiFile.findElementAt(editor.caretModel.offset)
            ?: return false

        val type = PsiTreeUtil.getContextOfType(element, PsiClass::class.java)
        return type!=null
            && element is PsiIdentifier
            && element.parent == type
    }

    override fun isVisible(event: AnActionEvent)
        = isEnabled(event)

    override fun presentation(event: AnActionEvent): String {
        val psiFile = event.getData(CommonDataKeys.PSI_FILE)!!
        val editor = event.getData(CommonDataKeys.EDITOR)!!
        val element = psiFile.findElementAt(editor.caretModel.offset)!!
        val type = PsiTreeUtil.getContextOfType(element, PsiClass::class.java)!!
        return if (COPY == action)
                "Copy Json sample from ${type.name}"
            else
                "Preview Json sample from ${type.name}"
    }

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

