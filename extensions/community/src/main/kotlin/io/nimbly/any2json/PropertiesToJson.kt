package io.nimbly.any2json

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.psi.PsiFile
import io.nimbly.any2json.EPrettyAction.COPY
import io.nimbly.any2json.EPrettyAction.PREVIEW
import io.nimbly.any2json.conversion.propertiesToMap
import io.nimbly.any2json.util.processPrettifierAction
import io.nimbly.any2json.util.selectedLines

class PropertiesToJsonCopy : PropertiesToJsonPrettifyOrCopy(COPY), Any2JsonCopyExtensionPoint

class PropertiesToJsonPreview : PropertiesToJsonPrettifyOrCopy(PREVIEW), Any2JsonPreviewExtensionPoint

open class PropertiesToJsonPrettifyOrCopy(private val action: EPrettyAction) : Any2JsonRootExtensionPoint {

    override fun process(event: AnActionEvent): Boolean {

        if (!isVisible(event))
            return false

        val project = event.project ?: return false
        val psiFile = event.getData(CommonDataKeys.PSI_FILE) ?: return false
        val editor = event.getData(CommonDataKeys.EDITOR)
        val selection = editor?.let { editor.selectedLines() }

        val content = selection ?: psiFile.text

        // Extract json
        val prettified = toJson(propertiesToMap(content, EType.SECONDARY))

        // Proceed
        return processPrettifierAction(action, prettified, project, event.dataContext)
    }

    override fun isVisible(event: AnActionEvent): Boolean {
        val psiFile : PsiFile = event.getData(CommonDataKeys.PSI_FILE) ?: return false
        if (! psiFile.name.toLowerCase().endsWith(".properties"))
            return false
        return true
    }

    override fun isEnabled(event: AnActionEvent)
        = isVisible(event)
}