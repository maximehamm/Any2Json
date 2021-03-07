package io.nimbly.any2json.util

import com.intellij.json.JsonLanguage
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFileFactory
import io.nimbly.any2json.EPrettyAction
import io.nimbly.any2json.info
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

fun processPrettifierAction(
    action: EPrettyAction,
    json: String,
    project: Project,
    dataContext: DataContext
): Boolean {

    if (action == EPrettyAction.COPY) {
        Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(json), StringSelection(json))
        info("Json prettified and copied to clipboard !", project)
        return true
    }

    if (action == EPrettyAction.PREVIEW) {
        val file = PsiFileFactory.getInstance(project).createFileFromText(
            "Preview.json", JsonLanguage.INSTANCE, json
        )
        openInSplittedTab(file, dataContext)
        return true
    }

    return false
}