package io.nimbly.any2json

import com.intellij.lang.javascript.psi.JSType
import com.intellij.lang.javascript.psi.ecma6.TypeScriptInterface
import com.intellij.lang.javascript.psi.ecma6.TypeScriptStringLiteralType
import com.intellij.lang.javascript.psi.ecma6.TypeScriptUnionOrIntersectionType
import com.intellij.lang.javascript.psi.types.JSArrayType
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import io.nimbly.any2json.EAction.COPY
import io.nimbly.any2json.EAction.PREVIEW
import io.nimbly.any2json.util.processAction

class TypeScript2JsonGeneratePreview : AbstractTypeScript2JsonGenerate(PREVIEW), Any2JsonPreviewExtensionPoint

class TypeScript2JsonGenerateCopy : AbstractTypeScript2JsonGenerate(COPY), Any2JsonCopyExtensionPoint

abstract class AbstractTypeScript2JsonGenerate(private val action: EAction) : Any2JsonRootExtensionPoint {

    override fun process(event: AnActionEvent): Boolean {

        val editor = event.getData(CommonDataKeys.EDITOR) ?: return false
        val project = event.project ?: return false
        val psiFile = event.getData(CommonDataKeys.PSI_FILE) ?: return false
        val element = psiFile.findElementAt(editor.caretModel.offset)

        val type = PsiTreeUtil.getContextOfType(element, TypeScriptInterface::class.java)
            ?: return false

        val map = type.fields
            .map { it.name to parse(
                it.jsType,
                it.initializer?.text)}
            .filter { it.second != null }
            .toMap()

        val json = toJson(map)

        return processAction(action, json, project, event.dataContext)
    }


    @Suppress("NAME_SHADOWING")
    private fun parse(
        type: JSType?,
        initializer: String?,
        actionType: EType = EType.SECONDARY,
        done: MutableSet<JSType> = mutableSetOf()
    ): Any? {
        val initializer = if (initializer == "null") null else initializer

        val type = type
            ?: return null

        // Enum
        val t = type.asRecordType().sourceElement
        if (t is TypeScriptUnionOrIntersectionType) {
            val first = t.types.first()
            if (first is TypeScriptStringLiteralType)
                return first.innerText ?: ""
            return ""
        }

        // Known object with generator
        val typeName = type.resolvedTypeText
        GENERATORS[typeName]?.let {
            return it.generate(actionType == EType.SECONDARY, initializer)
        }

        // Array
        if (type is JSArrayType) {
            return listOfNotNull(parse(type.type, null, actionType, done))
        }

        // Avoid stack overflow
        if (done.contains(type))
            return null
        done.add(type)

        // Recurse
        return type.asRecordType().properties.map {
            it.memberName to parse(
                it.jsType, null, actionType, done)
        }.toMap()
    }

    override fun isEnabled(event: AnActionEvent): Boolean {
        val psiFile : PsiFile = event.getData(CommonDataKeys.PSI_FILE) ?: return false
        if (! psiFile.name.endsWith(".ts"))
            return false

        val editor = event.getData(CommonDataKeys.EDITOR) ?:
        return false

        val element = psiFile.findElementAt(editor.caretModel.offset)
            ?: return false

        val type = PsiTreeUtil.getContextOfType(element, TypeScriptInterface::class.java)
        return type!=null
            && element.parent == type
    }

    override fun isVisible(event: AnActionEvent)
        = isEnabled(event)

    override fun presentation(event: AnActionEvent): String {
        val psiFile = event.getData(CommonDataKeys.PSI_FILE)!!
        val editor = event.getData(CommonDataKeys.EDITOR)!!
        val element = psiFile.findElementAt(editor.caretModel.offset)!!
        val type = PsiTreeUtil.getContextOfType(element, TypeScriptInterface::class.java)!!
        return if (COPY == action)
                "Copy Json sample from ${type.name}"
            else
                "Preview Json sample from ${type.name}"
    }

    companion object {
        val GENERATORS = mapOf(
            "Boolean" to GBoolean(),
            "Null" to GNull(), "Unknown" to GNull(),
            "Object" to GObject(), "any" to GObject(),
            "Number" to GInteger(), "BigInt" to GLong(),
            "String" to GString(), "Color" to GString()
        )
    }
}

