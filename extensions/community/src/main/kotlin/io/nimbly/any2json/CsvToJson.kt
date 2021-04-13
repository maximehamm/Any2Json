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

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.psi.PsiFile
import io.nimbly.any2json.EAction.COPY
import io.nimbly.any2json.EAction.PREVIEW
import io.nimbly.any2json.conversion.csvToMap
import io.nimbly.any2json.conversion.toJson
import io.nimbly.any2json.util.line
import io.nimbly.any2json.util.processAction
import io.nimbly.any2json.util.selectedLines

class CsvToJsonCopy : CsvToJsonPrettifyOrCopy(COPY), Any2JsonCopyExtensionPoint

class CsvToJsonPreview : CsvToJsonPrettifyOrCopy(PREVIEW), Any2JsonPreviewExtensionPoint

open class CsvToJsonPrettifyOrCopy(private val action: EAction) : Any2JsonRootExtensionPoint {

    override fun process(event: AnActionEvent): Boolean {

        if (!isVisible(event))
            return false

        val project = event.project ?: return false
        val psiFile : PsiFile = event.getData(CommonDataKeys.PSI_FILE) ?: return false
        val editor = event.getData(CommonDataKeys.EDITOR)

        val selection = editor?.let { editor.selectedLines() }

        val content =
            if (selection != null) {
                editor.line(0) + '\n' + selection
            } else {
                psiFile.text
            }

        // Extract json
        val prettified = toJson(csvToMap(content))

        // Proceed
        return processAction(action, prettified, project, event.dataContext)
    }

    override fun isVisible(event: AnActionEvent): Boolean {
        val psiFile : PsiFile = event.getData(CommonDataKeys.PSI_FILE) ?: return false
        if (! psiFile.name.toLowerCase().endsWith(".csv"))
            return false
        return true
    }

    override fun isEnabled(event: AnActionEvent)
        = isVisible(event)
}