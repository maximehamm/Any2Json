package io.nimbly.any2json.util

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx
import com.intellij.openapi.fileEditor.impl.EditorWindow
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiFile
import javax.swing.SwingConstants

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

    // if we got a valid symbol we will open it in a splitted tab, else we call the GotoDeclarationAction
    val fileEditorManager = FileEditorManagerEx.getInstanceEx(file.project)!!
    val nextWindowPane = splitPane(file.project, fileEditorManager, dataContext) ?: return
    fileEditorManager.currentWindow = nextWindowPane

    val fileToClose = fileEditorManager.currentFile!!
    nextWindowPane.manager.openFileImpl2(nextWindowPane, file.containingFile.virtualFile, true)
    fileEditorManager.currentWindow.closeFile(fileToClose)
}

private fun splitPane(project: Project, fileEditorManager: FileEditorManagerEx, dataContext: DataContext): EditorWindow? {
    val activePane = EditorWindow.DATA_KEY.getData(dataContext) ?: return null
    var pane = fileEditorManager.getNextWindow(activePane)
    if (pane === activePane) {
        val fileManagerEx = FileEditorManagerEx.getInstance(project) as FileEditorManagerEx
        fileManagerEx.createSplitter(SwingConstants.VERTICAL, fileManagerEx.currentWindow)
        pane = fileEditorManager.getNextWindow(activePane)
    }
    return pane
}