package io.nimbly.any2json

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class Any2JsonJavaPrettifyAction : AnAction() {

    override fun actionPerformed(event: AnActionEvent) {
        PRETTIFY().extensionList.find {
            it.prettify(event)
        }
    }

    override fun update(event: AnActionEvent) {
        val enabled =
            PRETTIFY().extensionList.find {
                it.isEnabled(event)
            } != null
        event.presentation.isVisible = enabled
        event.presentation.isEnabled = enabled
    }
}