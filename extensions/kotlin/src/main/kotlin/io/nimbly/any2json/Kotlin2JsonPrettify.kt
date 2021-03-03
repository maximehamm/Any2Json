package io.nimbly.any2json

import com.intellij.application.options.CodeStyle
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.LanguageIndentStrategy
import com.intellij.openapi.editor.actions.EditorActionUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiLiteralUtil
import com.intellij.util.DocumentUtil
import io.nimbly.any2json.EPrettyAction.COPY
import org.jetbrains.kotlin.idea.intentions.copyConcatenatedStringToClipboard.ConcatenatedStringGenerator
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.endOffset
import org.jetbrains.kotlin.psi.psiUtil.startOffset
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.util.*

class Kotlin2JsonPrettify : Kotlin2JsonPrettifyOrCopy(EPrettyAction.REPLACE), Any2JsonPrettifyExtensionPoint

class Kotlin2JsonCopy : Kotlin2JsonPrettifyOrCopy(COPY), Any2JsonCopyExtensionPoint

open class Kotlin2JsonPrettifyOrCopy(private val action: EPrettyAction) : Any2JsonRootExtensionPoint {

    override fun prettify(event: AnActionEvent): Boolean {

        val literal = getLiteral(event) ?: return false
        val project = event.project ?: return false
        val editor = event.getData(CommonDataKeys.EDITOR) ?: return false
        val document = editor.document
        val literalParent = literal.parent

        var oldElement: PsiElement
        val content: String
        if (literalParent is KtBinaryExpression) {
            content = ConcatenatedStringGenerator().create(literalParent)
            oldElement = literalParent
            while (oldElement.parent is KtBinaryExpression) {
                oldElement = oldElement.parent
            }
        } else {
            content = (literal.text ?: return false)
            oldElement = literal
        }

        // Extract json
        val json =
            if (content.startsWith("\"\"\"")) {
                content.substring(3, content.length - 3)
            }
            else  if (content.startsWith("\"")) {
                content.substring(1, content.length - 1)
            }
            else {
                prettify(content)
            }

        val prettify = prettify(json)
        if (action == COPY) {
            Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(prettify), StringSelection(prettify))
            return true
        }

        var prettified =
            PsiLiteralUtil.escapeBackSlashesInTextBlock(prettify)

        // Pretify
        val countLines = prettified.count { it == '\n' }
        prettified = if (countLines>0) {
            "\"\"\"" + prettified + "\"\"\""
        }
        else {
            "\"" + prettified + "\""
        }

        // Get text after literal
        val startLine = document.getLineNumber(oldElement.startOffset)
        val endline = document.getLineEndOffset(document.getLineNumber(oldElement.endOffset))
        val lineEnds = document.getText(TextRange(oldElement.endOffset, endline))

        // Instanciate new expression
        val trimIndent = !lineEnds.startsWith(".trimIndent()") && countLines>1
        val newExp = if (trimIndent) {
            KtPsiFactory(project).createExpressionIfPossible("$prettified.trimIndent()")!!
        } else {
            KtPsiFactory(project).createExpressionIfPossible(prettified)!!
        }

        // Replace literal and indent new content
        WriteCommandAction.runWriteCommandAction(project) {

            // Replace literal
            val replaced = oldElement.replace(newExp)

            // commit
            PsiDocumentManager.getInstance(project).commitDocument(document)
            PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(document)

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
        val psiFile = event.getData(CommonDataKeys.PSI_FILE) ?: return null
        val editor = event.getData(CommonDataKeys.EDITOR) ?: return null
        val element = psiFile.findElementAt(editor.caretModel.offset) ?: return null
        val parent1 = element.parent ?: return null
        if (parent1 is KtLiteralStringTemplateEntry || parent1 is KtEscapeStringTemplateEntry) {
            val parent2 = parent1.parent
            if (parent2 !is KtStringTemplateExpression)
                return null
            return parent2
        }
        return null
    }

}