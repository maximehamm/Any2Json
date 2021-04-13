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
import com.jetbrains.python.psi.PyParenthesizedExpression
import com.jetbrains.python.psi.impl.PyStringLiteralExpressionImpl
import io.nimbly.any2json.EAction.COPY
import io.nimbly.any2json.conversion.convertToJson
import io.nimbly.any2json.util.info
import io.nimbly.any2json.util.openInSplittedTab
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

class Python2JsonPrettify : Python2JsonPrettifyOrCopy(EAction.REPLACE), Any2JsonPrettifyExtensionPoint

class Python2JsonCopy : Python2JsonPrettifyOrCopy(COPY), Any2JsonCopyExtensionPoint

class Python2JsonPreview : Python2JsonPrettifyOrCopy(EAction.PREVIEW), Any2JsonPreviewExtensionPoint

open class Python2JsonPrettifyOrCopy(private val action: EAction) : Any2JsonRootExtensionPoint {

    override fun process(event: AnActionEvent): Boolean {

        val literal = getLiteral(event) ?: return false
        val project = event.project ?: return false
        val editor = event.getData(CommonDataKeys.EDITOR) ?: return false
        val document = editor.document
        val json = literal.stringValue;
        val parent = literal.parent

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

    override fun isVisible(event: AnActionEvent)
        = getLiteral(event) != null

    override fun isEnabled(event: AnActionEvent)
            = isVisible(event) // TODO DO AS JAVA

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