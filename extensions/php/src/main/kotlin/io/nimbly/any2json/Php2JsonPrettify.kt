/*
 * ANY2JSON
 * Copyright (C) 2021  Maxime HAMM - NIMBLY CONSULTING - maxime.hamm.pro@gmail.com
 *
 * This document is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This work is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package io.nimbly.any2json

import com.intellij.json.JsonLanguage
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFileFactory
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import io.nimbly.any2json.EAction.COPY
import io.nimbly.any2json.conversion.convertToJson
import io.nimbly.any2json.util.info
import io.nimbly.any2json.util.openInSplittedTab
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

class Php2JsonPrettify : Php2JsonPrettifyOrCopy(EAction.REPLACE), Any2JsonPrettifyExtensionPoint

class Php2JsonCopy : Php2JsonPrettifyOrCopy(COPY), Any2JsonCopyExtensionPoint

class Php2JsonPreview : Php2JsonPrettifyOrCopy(EAction.PREVIEW), Any2JsonPreviewExtensionPoint

open class Php2JsonPrettifyOrCopy(private val action: EAction) : Any2JsonRootExtensionPoint {

    override fun process(event: AnActionEvent): Boolean {

        val literal = getLiteral(event) ?: return false
        val project = event.project ?: return false
        val editor = event.getData(CommonDataKeys.EDITOR) ?: return false
        val document = editor.document
        val json = literal.contents;

        // Extract json
        val prettified = convertToJson(json)
        if (action == COPY) {
            Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(prettified), StringSelection(prettified))
            info("Json prettified and copied to clipboard !", project)
            return true
        }

        if (action == EAction.PREVIEW) {
            val file = PsiFileFactory.getInstance(project).createFileFromText(
                "Preview.json", JsonLanguage.INSTANCE, prettified)
            openInSplittedTab(file, event.dataContext)
            return true
        }

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

    override fun isVisible(event: AnActionEvent)
        = getLiteral(event) != null

    override fun isEnabled(event: AnActionEvent)
            = isVisible(event)

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