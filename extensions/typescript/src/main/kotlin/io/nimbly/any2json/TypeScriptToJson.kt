package io.nimbly.any2json

import com.intellij.lang.javascript.psi.JSType
import com.intellij.lang.javascript.psi.ecma6.TypeScriptInterface
import com.intellij.lang.javascript.psi.ecma6.TypeScriptStringLiteralType
import com.intellij.lang.javascript.psi.ecma6.TypeScriptUnionOrIntersectionType
import com.intellij.lang.javascript.psi.types.JSArrayType
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys.EDITOR
import com.intellij.openapi.actionSystem.CommonDataKeys.PSI_FILE
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil

class TypeScriptToJson : Any2JsonExtensionPoint<String> {

    @Suppress("UNCHECKED_CAST")
    override fun build(event: AnActionEvent, actionType: EType) : Pair<String, Map<String, Any>>? {

        if (!isEnabled(event, actionType))
            return null

        val editor = event.getData(EDITOR) ?: return null
        val psiFile = event.getData(PSI_FILE) ?: return null
        val element = psiFile.findElementAt(editor.caretModel.offset)

        val type = PsiTreeUtil.getContextOfType(element, TypeScriptInterface::class.java)
            ?: return null

        return type.name!! to type.fields
            .map { it.name to parse(
                it.jsType, 
                it.initializer?.text,
                actionType)}
            .filter { it.second != null }
            .toMap() as Map<String, Any>
    }

    @Suppress("NAME_SHADOWING")
    private fun parse(
        type: JSType?,
        initializer: String?,
        actionType: EType,
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



    override fun isEnabled(event: AnActionEvent, actionType: EType): Boolean {
        val psiFile : PsiFile = event.getData(PSI_FILE) ?: return false
        return psiFile.name.endsWith(".ts")
    }

    override fun presentation(actionType: EType)
            = "from Type" + if (actionType == EType.SECONDARY) " with Data" else ""

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