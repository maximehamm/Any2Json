package io.nimbly.any2json

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys.PSI_FILE
import com.intellij.psi.PsiFile

class YamlToJson : Any2JsonExtensionPoint {

    @Suppress("UNCHECKED_CAST")
    override fun build(event: AnActionEvent, actionType: EType) : Pair<String, List<Map<String, Any>>>? {

        if (!isEnabled(event, actionType))
            return null

        val psiFile = event.getData(PSI_FILE) ?: return null
        return psiFile.name to yamlToJson(psiFile.text)
    }

    override fun isEnabled(event: AnActionEvent, actionType: EType): Boolean {
        if (actionType != EType.MAIN)
            return false

        val psiFile : PsiFile = event.getData(PSI_FILE) ?: return false
        if (!psiFile.name.endsWith(".yaml") && !psiFile.name.endsWith(".yml"))
            return false

        return true
    }

    override fun presentation(actionType: EType, event: AnActionEvent): String {
        return "from YAML"
    }
}