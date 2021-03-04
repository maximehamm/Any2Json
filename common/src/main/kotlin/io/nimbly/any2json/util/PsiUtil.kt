package io.nimbly.any2json.util

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.TextRange
import com.intellij.util.DocumentUtil

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