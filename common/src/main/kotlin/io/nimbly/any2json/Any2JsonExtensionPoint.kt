package io.nimbly.any2json
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.xdebugger.impl.ui.tree.nodes.XValueNodeImpl

interface Any2JsonExtensionPoint {
    fun build(event: AnActionEvent, actionType: EType) : Pair<String, Any>?
    fun isEnabled(event: AnActionEvent, actionType: EType): Boolean
    fun presentation(actionType: EType, event: AnActionEvent): String
}

interface Any2JsonDebuggerExtensionPoint {
    fun loadProperty(node: XValueNodeImpl): Pair<Boolean, Any?>
}

interface Any2JsonPrettifyExtensionPoint : Any2JsonRootExtensionPoint

interface Any2JsonCopyExtensionPoint : Any2JsonRootExtensionPoint

interface Any2JsonPreviewExtensionPoint : Any2JsonRootExtensionPoint

interface Any2JsonRootExtensionPoint {
    fun isVisible(event: AnActionEvent): Boolean
    fun isEnabled(event: AnActionEvent): Boolean
    fun process(event: AnActionEvent): Boolean
}

enum class EPrettyAction { COPY, REPLACE, PREVIEW }

enum class EType { MAIN, SECONDARY }