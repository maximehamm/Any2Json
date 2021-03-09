package io.nimbly.any2json

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.xml.XmlTag
import io.nimbly.any2json.EPrettyAction.COPY
import io.nimbly.any2json.EPrettyAction.PREVIEW
import io.nimbly.any2json.util.processPrettifierAction

class XmlToJsonCopy : XmlToJsonPrettifyOrCopy(COPY), Any2JsonCopyExtensionPoint

class XmlToJsonPreview : XmlToJsonPrettifyOrCopy(PREVIEW), Any2JsonPreviewExtensionPoint

open class XmlToJsonPrettifyOrCopy(private val action: EPrettyAction) : Any2JsonRootExtensionPoint {

    override fun process(event: AnActionEvent): Boolean {

        if (!isVisible(event))
            return false

        val project = event.project ?: return false
        val psiFile : PsiFile = event.getData(CommonDataKeys.PSI_FILE) ?: return false
        val editor = event.getData(CommonDataKeys.EDITOR)
        val element = editor?.let { psiFile.findElementAt(editor.caretModel.offset) }
        val xmlTag = PsiTreeUtil.getContextOfType(element, XmlTag::class.java)

        val content =
            if (xmlTag !=null) xmlTag.text
            else psiFile.text

        // Extract json
        val prettified = toJson(xmlToJson(content).toMap())

        // Proceed
        return processPrettifierAction(action, prettified, project, event.dataContext)
    }

    override fun isVisible(event: AnActionEvent): Boolean {
        val psiFile : PsiFile = event.getData(CommonDataKeys.PSI_FILE) ?: return false
        if (! psiFile.name.toLowerCase().endsWith(".xml"))
            return false
        return true
    }

    override fun isEnabled(event: AnActionEvent)
        = isVisible(event)
}