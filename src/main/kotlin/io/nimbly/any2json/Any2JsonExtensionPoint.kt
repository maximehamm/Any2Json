package io.nimbly.any2json
import com.intellij.openapi.actionSystem.AnActionEvent

interface Any2JsonExtensionPoint<T : Any> {

    fun build(e: AnActionEvent) : Pair<String, Map<String, Any>>?

    fun isEnabled(event: AnActionEvent, generateValues: Boolean): Boolean

    fun presentation() : String
}