package io.nimbly.any2json

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.psi.PsiFile
import io.nimbly.any2json.EPrettyAction.COPY
import io.nimbly.any2json.EPrettyAction.PREVIEW
import io.nimbly.any2json.util.line
import io.nimbly.any2json.util.processPrettifierAction
import io.nimbly.any2json.util.selectedLines

class CsvToJsonCopy : CsvToJsonPrettifyOrCopy(COPY), Any2JsonCopyExtensionPoint

class CsvToJsonPreview : CsvToJsonPrettifyOrCopy(PREVIEW), Any2JsonPreviewExtensionPoint

open class CsvToJsonPrettifyOrCopy(private val action: EPrettyAction) : Any2JsonRootExtensionPoint {

    override fun prettify(event: AnActionEvent): Boolean {

        if (!isVisible(event))
            return false

        val project = event.project ?: return false
        val psiFile : PsiFile = event.getData(CommonDataKeys.PSI_FILE) ?: return false
        val editor = event.getData(CommonDataKeys.EDITOR)

        val selection = editor?.let { editor.selectedLines() }

        val content =
            if (selection != null) {
                editor.line(0) + '\n' + selection
            } else {
                psiFile.text
            }

        // Extract json
        val prettified = toJson(csvToMap(content))

        // Proceed
        return processPrettifierAction(action, prettified, project, event.dataContext)
    }

    override fun isVisible(event: AnActionEvent): Boolean {
        val psiFile : PsiFile = event.getData(CommonDataKeys.PSI_FILE) ?: return false
        if (! psiFile.name.toLowerCase().endsWith(".csv"))
            return false
        return true
    }

    override fun isEnabled(event: AnActionEvent)
        = isVisible(event)
}