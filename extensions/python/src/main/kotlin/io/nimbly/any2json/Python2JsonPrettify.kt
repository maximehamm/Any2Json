package io.nimbly.any2json

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.PsiDocumentManager
import com.jetbrains.python.psi.PyParenthesizedExpression
import com.jetbrains.python.psi.StringLiteralExpression
import com.jetbrains.python.psi.impl.PyStringLiteralExpressionImpl

class Python2JsonPrettify : Any2JsonPrettifyExtensionPoint {

    override fun prettify(event: AnActionEvent): Boolean {

        val literal = getLiteral(event) ?: return false
        val project = event.project ?: return false
        val editor = event.getData(CommonDataKeys.EDITOR) ?: return false
        val document = editor.document
        val json = literal.stringValue;
        val parent = literal.parent

        // Extract json
        val prettified = prettify(json)

        // Replace literal and indent new content
        WriteCommandAction.runWriteCommandAction(project) {

            // Replace literal
            val newLiteral = literal.updateText("\"\"\"" + prettified + "\"\"\"")
            //PsiDocumentManager.getInstance(project).commitDocument(document)

            // Remove parenthesis
            if (parent is PyParenthesizedExpression) {
                parent.replace(newLiteral)
            }

            // commit
            PsiDocumentManager.getInstance(project).commitDocument(document)
            PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(document)
        }

        return true
    }

    override fun isEnabled(event: AnActionEvent)
        = getLiteral(event) != null

    private fun getLiteral(event: AnActionEvent): PyStringLiteralExpressionImpl? {
        val psiFile = event.getData(CommonDataKeys.PSI_FILE) ?: return null
        val editor = event.getData(CommonDataKeys.EDITOR) ?: return null
        val element = psiFile.findElementAt(editor.caretModel.offset) ?: return null
        val parent1 = element.parent ?: return null
        if (parent1 is PyStringLiteralExpressionImpl) {
            return parent1
        }
        return null
    }

}