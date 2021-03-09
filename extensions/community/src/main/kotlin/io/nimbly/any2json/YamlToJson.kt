package io.nimbly.any2json

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.psi.PsiFile
import io.nimbly.any2json.EAction.COPY
import io.nimbly.any2json.EAction.PREVIEW
import io.nimbly.any2json.util.processAction

class YamlToJsonCopy : YamlToJsonPrettifyOrCopy(COPY), Any2JsonCopyExtensionPoint

class YamlToJsonPreview : YamlToJsonPrettifyOrCopy(PREVIEW), Any2JsonPreviewExtensionPoint

open class YamlToJsonPrettifyOrCopy(private val action: EAction) : Any2JsonRootExtensionPoint {

    override fun process(event: AnActionEvent): Boolean {

        if (!isVisible(event))
            return false

        val project = event.project ?: return false
        val psiFile : PsiFile = event.getData(CommonDataKeys.PSI_FILE) ?: return false

        // Extract json
        val prettified = toJson(yamlToJson(psiFile.text))

        // Proceed
        return processAction(action, prettified, project, event.dataContext)
    }

    override fun isVisible(event: AnActionEvent): Boolean {
        val psiFile : PsiFile = event.getData(CommonDataKeys.PSI_FILE) ?: return false
        if (! psiFile.name.toLowerCase().endsWith(".yaml"))
            return false
        return true
    }

    override fun isEnabled(event: AnActionEvent)
        = isVisible(event)
}