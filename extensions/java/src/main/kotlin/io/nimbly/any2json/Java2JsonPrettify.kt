package io.nimbly.any2json

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementFactory
import com.intellij.psi.PsiExpression
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiLiteralExpression
import com.intellij.psi.PsiPolyadicExpression
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.util.PsiLiteralUtil
import com.intellij.psi.util.PsiTreeUtil
import com.siyeh.ig.psiutils.ExpressionUtils
import java.lang.StringBuilder

class Java2JsonPrettify : Any2JsonPrettifyExtensionPoint {

    override fun prettify(event: AnActionEvent): Boolean {

        val l = getLiteral(event) ?:return false
        val project = event.project ?: return false

        val content: String
        val toReplace: PsiElement
        val type = PsiTreeUtil.getContextOfType(l, PsiPolyadicExpression::class.java)
        if (type !=null) {
           content = buildConcatenationText(type)
           toReplace = type
        }
        else {
            val v = l.value ?: return false
            if (v !is String) return false
            toReplace = l
            content = v
        }

        val prettified = "\"" + PsiLiteralUtil.escapeBackSlashesInTextBlock(prettify(content))
            .replace("\"", "\\\"")
            .replace("\n", "\\n\"+\n\"") + "\""

        WriteCommandAction.runWriteCommandAction(project) {
            val factory: PsiElementFactory = JavaPsiFacade.getInstance(project).getElementFactory()
            val newElement = factory.createExpressionFromText(prettified, null)
            toReplace.replace(newElement)

            newElement.toString()
        }

        return true
    }


    override fun isEnabled(event: AnActionEvent)
        = getLiteral(event) != null


    private fun getLiteral(event: AnActionEvent): PsiLiteralExpression? {
        val psiFile = event.getData(CommonDataKeys.PSI_FILE) ?: return null
        val editor = event.getData(CommonDataKeys.EDITOR) ?: return null
        val element = psiFile.findElementAt(editor.caretModel.offset) ?: return null
        val parent = element.parent ?: return null
        if (parent !is PsiLiteralExpression)
            return null
        return parent
    }

    private fun buildConcatenationText(polyadicExpression: PsiPolyadicExpression): String {
        val out = StringBuilder()
        var element = polyadicExpression.firstChild
        while (element != null) {
            if (element is PsiExpression) {
                val value = ExpressionUtils.computeConstantExpression(element)
                out.append(value?.toString() ?: "?")
            }
            else if (element is PsiWhiteSpace && element.getText().contains("\n") &&
                    (out.isEmpty() || out[out.length - 1] != '\n')) {
                out.append('\n')
            }
            element = element.nextSibling
        }
        return out.toString()
    }
}