package io.nimbly.extension.python

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import io.nimbly.any2json.Any2JsonExtensionPoint

class PythonToJavaImpl : Any2JsonExtensionPoint<String> {

    override fun build(e: AnActionEvent) : Pair<String, Map<String, Any>>? {

//        val editor = e.getData(CommonDataKeys.EDITOR) ?: return null
//        val psiFile = e.getData(CommonDataKeys.PSI_FILE) ?: return null
//        val element = psiFile.findElementAt(editor.caretModel.offset)

        return null
//        if (element !is Python)
//        return null
//        return Pair("NULL", emptyMap())
    }

    override fun isEnabled(event: AnActionEvent, generateValues: Boolean): Boolean {
        return false
    }

    override fun presentation() = "from Class"

}