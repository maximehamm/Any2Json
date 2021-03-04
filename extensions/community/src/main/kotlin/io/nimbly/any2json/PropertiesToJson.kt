package io.nimbly.any2json

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.CommonDataKeys.PSI_FILE
import com.intellij.psi.PsiFile
import io.nimbly.any2json.conversion.propertiesToMap
import io.nimbly.any2json.util.selectedLines

class PropertiesToJson : Any2JsonExtensionPoint {

    @Suppress("UNCHECKED_CAST")
    override fun build(event: AnActionEvent, actionType: EType) : Pair<String, Any>? {

        if (!isEnabled(event, actionType))
            return null

        val psiFile = event.getData(PSI_FILE) ?: return null
        val editor = event.getData(CommonDataKeys.EDITOR)
        val selection = editor?.let { editor.selectedLines() }

        val content = selection ?: psiFile.text

        return psiFile.name to propertiesToMap(content, actionType)
    }

    override fun isEnabled(event: AnActionEvent, actionType: EType): Boolean {
        val psiFile : PsiFile = event.getData(PSI_FILE) ?: return false
        if (! psiFile.name.toLowerCase().endsWith(".properties"))
            return false
        return true
    }

    override fun presentation(actionType: EType, event: AnActionEvent): String {
        return "from PROPERTIES" + if (actionType == EType.MAIN) " (flat)" else " (hierarchical)"
    }
}
