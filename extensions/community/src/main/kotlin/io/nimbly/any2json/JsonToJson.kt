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

import com.google.gson.JsonParser
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import io.nimbly.any2json.EAction.COPY
import io.nimbly.any2json.conversion.toJson
import io.nimbly.any2json.util.processAction

class JsonPrettify : JsonPrettifyOrCopy(EAction.REPLACE), Any2JsonPrettifyExtensionPoint

class JsonCopy : JsonPrettifyOrCopy(COPY), Any2JsonCopyExtensionPoint

open class JsonPrettifyOrCopy(private val action: EAction) : Any2JsonRootExtensionPoint {

    override fun process(event: AnActionEvent): Boolean {

        if (!isVisible(event))
            return false

        val project = event.project ?: return false
        val editor = event.getData(CommonDataKeys.EDITOR) ?: return false
        val document = editor.document
        val psiFile : PsiFile = event.getData(CommonDataKeys.PSI_FILE) ?: return false

        val json = psiFile.text;

        // Extract json
        val prettified = toJson(JsonParser.parseString(json))

        // Proceed
        val done = processAction(action, prettified, project, event.dataContext)
        if (done)
            return true

        // Replace literal and indent new content
        WriteCommandAction.runWriteCommandAction(project) {

            // Replace literal
            document.setText(prettified)

            // commit
            PsiDocumentManager.getInstance(project).commitDocument(document)
            PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(document)
        }

        return true
    }

    override fun isVisible(event: AnActionEvent): Boolean {
        val psiFile : PsiFile = event.getData(CommonDataKeys.PSI_FILE) ?: return false
        if (! psiFile.name.toLowerCase().endsWith(".json"))
            return false
        return true
    }

    override fun isEnabled(event: AnActionEvent)
            = isVisible(event)

}