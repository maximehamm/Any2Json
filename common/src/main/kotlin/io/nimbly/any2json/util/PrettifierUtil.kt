package io.nimbly.any2json.util

import com.intellij.json.JsonLanguage
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFileFactory
import io.nimbly.any2json.EAction
import io.nimbly.any2json.info
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