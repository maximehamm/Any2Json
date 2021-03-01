package io.nimbly.any2json

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.PsiDocumentManager
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression

class Php2JsonPrettify : Any2JsonPrettifyExtensionPoint {

    override fun prettify(event: AnActionEvent): Boolean {

        val literal = getLiteral(event) ?: return false
        val project = event.project ?: return false
        val editor = event.getData(CommonDataKeys.EDITOR) ?: return false
        val document = editor.document
        val json = literal.contents;

        // Extract json
        val prettified = prettify(json)

        // Replace literal and indent new content
        WriteCommandAction.runWriteCommandAction(project) {

            // Replace literal
            val i = literal.text.indexOf(json)
            if (literal.name != null) {
                val replaced = literal.text.substring(0, i) + prettified + literal.text.substring(i + json.length)
                literal.updateText(replaced)
            }
            else {
                literal.updateText(prettified)
            }

            // commit
            PsiDocumentManager.getInstance(project).commitDocument(document)
            PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(document)
        }

        return true
    }

    override fun isEnabled(event: AnActionEvent)
        = getLiteral(event) != null

    private fun getLiteral(event: AnActionEvent): StringLiteralExpression? {
        val psiFile = event.getData(CommonDataKeys.PSI_FILE) ?: return null
        val editor = event.getData(CommonDataKeys.EDITOR) ?: return null
        val element = psiFile.findElementAt(editor.caretModel.offset) ?: return null
        val parent1 = element.parent ?: return null
        if (parent1 is StringLiteralExpression) {
            return parent1
        }
        return null
    }

}