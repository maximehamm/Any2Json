package io.nimbly.any2json

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.CommonDataKeys.PSI_FILE
import com.intellij.psi.PsiFile
import io.nimbly.any2json.util.line
import io.nimbly.any2json.util.selectedLines

class CsvToJson : Any2JsonExtensionPoint {

    @Suppress("UNCHECKED_CAST")
    override fun build(event: AnActionEvent, actionType: EType) : Pair<String, List<Map<String, Any>>>? {

        if (!isEnabled(event, actionType))
            return null

        val psiFile = event.getData(PSI_FILE) ?: return null
        val editor = event.getData(CommonDataKeys.EDITOR)
        val selection = editor?.let { editor.selectedLines() }

        val content =
            if (selection != null) {
                editor.line(0) + '\n' + selection
            } else {
                psiFile.text
            }

        return psiFile.name to csvToMap(content)
    }

    override fun isEnabled(event: AnActionEvent, actionType: EType): Boolean {
        if (actionType != EType.MAIN)
            return false

        val psiFile : PsiFile = event.getData(PSI_FILE) ?: return false
        if (! psiFile.name.toLowerCase().endsWith(".csv"))
            return false

        return true
    }

    override fun presentation(actionType: EType, event: AnActionEvent): String {
        return "from CSV"
    }
}