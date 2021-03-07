package io.nimbly.any2json

import com.intellij.json.JsonLanguage
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.xml.XmlTag
import io.nimbly.any2json.EPrettyAction.COPY
import io.nimbly.any2json.EPrettyAction.PREVIEW
import io.nimbly.any2json.util.openInSplittedTab
import io.nimbly.any2json.util.processPrettierAction
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

class YamlToJsonCopy : YamlToJsonPrettifyOrCopy(COPY), Any2JsonCopyExtensionPoint

class YamlToJsonPreview : YamlToJsonPrettifyOrCopy(PREVIEW), Any2JsonPreviewExtensionPoint

open class YamlToJsonPrettifyOrCopy(private val action: EPrettyAction) : Any2JsonRootExtensionPoint {

    override fun prettify(event: AnActionEvent): Boolean {

        if (!isVisible(event))
            return false

        val project = event.project ?: return false
        val psiFile : PsiFile = event.getData(CommonDataKeys.PSI_FILE) ?: return false

        // Extract json
        val prettified = toJson(yamlToJson(psiFile.text))

        // Proceed
        return processPrettierAction(action, prettified, project, event.dataContext)
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