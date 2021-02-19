package io.nimbly.any2json

import com.intellij.application.options.CodeStyle
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.LanguageIndentStrategy
import com.intellij.openapi.editor.actions.EditorActionUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiLiteralUtil
import com.intellij.util.DocumentUtil
import org.jetbrains.kotlin.psi.KtLiteralStringTemplateEntry
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.KtStringTemplateExpression
import org.jetbrains.kotlin.psi.psiUtil.endOffset
import org.jetbrains.kotlin.psi.psiUtil.startOffset
import java.util.ArrayList

class Kotlin2JsonPrettify : Any2JsonPrettifyExtensionPoint {

    override fun prettify(event: AnActionEvent): Boolean {

        val literal = getLiteral(event) ?: return false
        val project = event.project ?: return false
        val editor = event.getData(CommonDataKeys.EDITOR) ?: return false
        var content = literal.text ?: return false
        val document = editor.document

        content = content.substring(3, content.length - 3)
        val prettified = "\"\"\"" + PsiLiteralUtil.escapeBackSlashesInTextBlock(prettify(content)) + "\"\"\""

        // Get text after literal
        val startLine = document.getLineNumber(literal.startOffset)
        val endline = document.getLineEndOffset(document.getLineNumber(literal.endOffset))
        val lineEnds = document.getText(TextRange(literal.endOffset, endline))

        // Instanciate new expression
        val newExp = if (lineEnds.startsWith(".trimIndent()")) {
            KtPsiFactory(project).createExpressionIfPossible(prettified)!!
        } else {
            KtPsiFactory(project).createExpressionIfPossible("$prettified.trimIndent()")!!
        }

        // Replace literal and indent new content
        ApplicationManager.getApplication().invokeLater {
            WriteCommandAction.runWriteCommandAction(project) {

                // Replace literal
                val replaced = literal.replace(newExp)

                // Search lines to indent
                val endLine = document.getLineNumber(replaced.endOffset)
                val lineStartOffset = DocumentUtil.getLineStartOffset(replaced.startOffset, document)
                val margin = DocumentUtil.getFirstNonSpaceCharOffset(document, startLine) - lineStartOffset

                // Do indent
                if (endLine > startLine && margin > 0) {

                    val blockIndent = CodeStyle.getIndentOptions(project, document).INDENT_SIZE
                    doIndent(endLine, startLine + 1, document, project, editor, margin + blockIndent)
                }
            }
        }

        return true
    }

    fun doIndent(
        endIndex: Int, startIndex: Int, document: Document, project: Project?, editor: Editor,
        blockIndent: Int) {
        val caretOffset = intArrayOf(editor.caretModel.offset)
        val selectionModel = editor.selectionModel
        val extendSelection = selectionModel.hasSelection() &&
                DocumentUtil.isAtLineStart(selectionModel.selectionStart, document) &&
                (selectionModel.selectionEnd == document.textLength ||
                        DocumentUtil.isAtLineStart(selectionModel.selectionEnd, document))
        val bulkMode = endIndex - startIndex > 50
        DocumentUtil.executeInBulk(document, bulkMode) {
            val nonModifiableLines: MutableList<Int> = ArrayList()
            if (project != null) {
                val indentationStartOffset = document.getLineStartOffset(startIndex)
                val indentationEndOffset = document.getLineStartOffset(endIndex)
                val file =
                    PsiDocumentManager.getInstance(project).getPsiFile(document)
                val indentStrategy =
                    LanguageIndentStrategy.getIndentStrategy(file)
                if (file != null && !LanguageIndentStrategy.isDefault(indentStrategy)) {
                    for (i in startIndex..endIndex) {
                        val element = file.findElementAt(document.getLineStartOffset(i))
                        if (element != null && !indentStrategy.canIndent(
                                indentationStartOffset,
                                indentationEndOffset,
                                element
                            )) {
                            nonModifiableLines.add(i)
                        }
                    }
                }
            }
            (startIndex..endIndex)
                .asSequence()
                .filterNot { nonModifiableLines.contains(it) }
                .forEach {
                    caretOffset[0] = EditorActionUtil.indentLine(
                        project,
                        editor,
                        it,
                        blockIndent,
                        caretOffset[0]
                    )
                }
        }
        if (extendSelection) {
            selectionModel.setSelection(
                DocumentUtil.getLineStartOffset(selectionModel.selectionStart, document),
                selectionModel.selectionEnd
            )
        }
        editor.caretModel.moveToOffset(caretOffset[0])
    }


    override fun isEnabled(event: AnActionEvent)
        = getLiteral(event) != null


    private fun getLiteral(event: AnActionEvent): KtStringTemplateExpression? {
        val psiFile : PsiFile = event.getData(CommonDataKeys.PSI_FILE) ?: return null
        val editor = event.getData(CommonDataKeys.EDITOR) ?: return null
        val element = psiFile.findElementAt(editor.caretModel.offset) ?: return null
        val parent1 = element.parent ?: return null
        if (parent1 !is KtLiteralStringTemplateEntry)
            return null
        val parent2 = parent1.parent
        if (parent2 !is KtStringTemplateExpression)
            return null
        return parent2
    }

}