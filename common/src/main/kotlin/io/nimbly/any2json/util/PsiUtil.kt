/*
 * ANY2JSON
 * Copyright (C) 2024  Maxime HAMM - NIMBLY CONSULTING - maxime.hamm.pro@gmail.com
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

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiFile

fun Editor.line(line: Int): String
    = document.getText(TextRange(0, document.getLineEndOffset(0)))

fun Editor.selectedLines(): String? {

    val sstart = selectionModel.selectionStart
    var send = selectionModel.selectionEnd

    if (send>0 && document.getText(TextRange(send -1, send)) == "\n")
        send -= 1;

    if (send <= sstart)
        return null

    val start = document.getLineStartOffset(
        document.getLineNumber(
            sstart
        ))

    val end = document.getLineEndOffset(
        document.getLineNumber(
            send
        ))

    return document.getText(TextRange(start, end))
}

fun openInSplittedTab(file: PsiFile, dataContext: DataContext) {

    FileEditorManager.getInstance(file.project)?.openFile(file.virtualFile)


//    // if we got a valid symbol we will open it in a splitted tab, else we call the GotoDeclarationAction
//    val fileEditorManager = FileEditorManagerEx.getInstanceEx(file.project)
//    val nextWindowPane = splitPane(file.project, fileEditorManager, dataContext)
//
//    if (nextWindowPane == null) {
//        file.navigate(true)
//        return
//    }
//
//    nextWindowPane.manager.openFileInNewWindow(file.containingFile.virtualFile)

//    fileEditorManager.currentWindow = nextWindowPane
//    val fileToClose = fileEditorManager.currentFile!!


//    nextWindowPane.manager.openFile(file.containingFile.virtualFile, nextWindowPane, FileEditorOpenOptions(requestFocus = true))
//    nextWindowPane.manager.closeFile(fileToClose)
//    nextWindowPane.manager.openFileImpl2(nextWindowPane, file.containingFile.virtualFile, true)
//    fileEditorManager.currentWindow.closeFile(fileToClose)
}

//private fun splitPane(project: Project, fileEditorManager: FileEditorManagerEx, dataContext: DataContext): EditorWindow? {
//    val activePane = EditorWindow.DATA_KEY.getData(dataContext) ?: return null
//    var pane = fileEditorManager.getNextWindow(activePane)
//    if (pane === activePane) {
//        val fileManagerEx = FileEditorManagerEx.getInstanceEx(project)
//        fileManagerEx.createSplitter(SwingConstants.VERTICAL, fileManagerEx.currentWindow)
//        pane = fileEditorManager.getNextWindow(activePane)
//    }
//    return pane
//}