package io.nimbly.any2json

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiElementFactory
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiLiteralExpression
import com.intellij.psi.util.PsiLiteralUtil

class Java2JsonPrettify : Any2JsonPrettifyExtensionPoint {

    override fun prettify(event: AnActionEvent): Boolean {

        val literal = getLiteral(event) ?:return false
        val project = event.project ?: return false
        val content = literal.value ?: return false
        if (content !is String) return false

        val prettified = "\"" + PsiLiteralUtil.escapeBackSlashesInTextBlock(prettify(content))
            .replace("\"", "\\\"")
            .replace("\n", "\"+\n\"") + "\""

        ApplicationManager.getApplication().invokeLater {
            WriteCommandAction.runWriteCommandAction(project) {
                val factory: PsiElementFactory = JavaPsiFacade.getInstance(project).getElementFactory()
                val newElement = factory.createExpressionFromText(prettified, null)
                literal.replace(newElement)
            }
        }

        return false
    }


    override fun isEnabled(event: AnActionEvent)
        = getLiteral(event) != null


    private fun getLiteral(event: AnActionEvent): PsiLiteralExpression? {
        val psiFile : PsiFile = event.getData(CommonDataKeys.PSI_FILE) ?: return null
        val editor = event.getData(CommonDataKeys.EDITOR) ?: return null
        val element = psiFile.findElementAt(editor.caretModel.offset) ?: return null
        val parent = element.parent ?: return null
        if (parent !is PsiLiteralExpression)
            return null
        return parent
    }

}