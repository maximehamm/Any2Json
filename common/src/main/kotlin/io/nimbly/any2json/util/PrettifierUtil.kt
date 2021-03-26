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

package io.nimbly.any2json.util

import com.intellij.json.JsonLanguage
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFileFactory
import io.nimbly.any2json.EAction
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

fun processAction(
    action: EAction,
    json: String,
    project: Project,
    dataContext: DataContext
): Boolean {

    if (action == EAction.COPY) {
        Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(json), StringSelection(json))
        info("Json prettified and copied to clipboard !", project)
        return true
    }

    if (action == EAction.PREVIEW) {
        val file = PsiFileFactory.getInstance(project).createFileFromText(
            "Preview.json", JsonLanguage.INSTANCE, json
        )
        openInSplittedTab(file, dataContext)
        return true
    }

    return false
}