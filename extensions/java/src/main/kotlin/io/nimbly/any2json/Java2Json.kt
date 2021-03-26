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
import com.intellij.psi.*
import com.intellij.psi.util.PsiLiteralUtil
import com.intellij.psi.util.PsiTreeUtil
import com.siyeh.ig.psiutils.ExpressionUtils
import io.nimbly.any2json.EAction.*
import io.nimbly.any2json.EAction.COPY
import io.nimbly.any2json.EAction.PREVIEW
import io.nimbly.any2json.util.info
import io.nimbly.any2json.util.openInSplittedTab
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

class Java2JsonReplace : AsbtractJava2JsonAction(REPLACE), Any2JsonPrettifyExtensionPoint

class Java2JsonCopy : AsbtractJava2JsonAction(COPY), Any2JsonCopyExtensionPoint

class Java2JsonPreview : AsbtractJava2JsonAction(PREVIEW), Any2JsonPreviewExtensionPoint

abstract class AsbtractJava2JsonAction(private val action: EAction) : Any2JsonRootExtensionPoint {

    override fun process(event: AnActionEvent): Boolean {

        val l = getLiteral(event) ?:return false
        val project = event.project ?: return false

        val (content, toReplace) = parse(l)
        if (content == null) return false

        val prettify = convertToJson(content)
        if (action == COPY) {
            Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(prettify), StringSelection(prettify))
            info("Json prettified and copied to clipboard !", project)
            return true
        }

        if (action == PREVIEW) {
            val file = PsiFileFactory.getInstance(project).createFileFromText(
                "Preview.json", JsonLanguage.INSTANCE, prettify)
            openInSplittedTab(file, event.dataContext)
            return true
        }

        if (toReplace == null) return false
        val text = "\"" + PsiLiteralUtil.escapeBackSlashesInTextBlock(prettify)
            .replace("\"", "\\\"")
            .replace("\n", "\\n\"+\n\"") + "\""

        WriteCommandAction.runWriteCommandAction(project) {
            val factory: PsiElementFactory = JavaPsiFacade.getInstance(project).getElementFactory()
            val newElement = factory.createExpressionFromText(text, null)
            toReplace.replace(newElement)

            newElement.toString()
        }

        return true
    }

    override fun isVisible(event: AnActionEvent): Boolean {
        val literal = getLiteral(event) ?: return false
        val (content, element) = parse(literal)
        return content!=null && element!=null
    }

    override fun isEnabled(event: AnActionEvent): Boolean {
        val literal = getLiteral(event) ?: return false
        val (content, _) = parse(literal)
        return content!=null
    }

    private fun parse(literal: PsiLiteralExpression): Pair<String?, PsiElement?> {

        val content: String
        val toReplace: PsiElement
        val type = PsiTreeUtil.getContextOfType(literal, PsiPolyadicExpression::class.java)
        if (type !=null) {
            content = buildConcatenationText(type)
            toReplace = type
        }
        else {
            val v = literal.value ?: return null to null
            if (v !is String) return null to null
            toReplace = literal
            content = v
        }
        return content to toReplace
    }

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