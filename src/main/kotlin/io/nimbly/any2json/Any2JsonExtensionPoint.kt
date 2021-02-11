package io.nimbly.any2json
import com.intellij.openapi.actionSystem.AnActionEvent

interface Any2JsonExtensionPoint<T : Any> {

    fun build(event: AnActionEvent, actionType: EType) : Pair<String, Map<String, Any>>?

    fun isEnabled(event: AnActionEvent, actionType: EType): Boolean

    fun presentation(actionType: EType): String
}