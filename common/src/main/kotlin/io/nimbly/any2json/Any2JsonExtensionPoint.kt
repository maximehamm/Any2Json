package io.nimbly.any2json
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.xdebugger.impl.ui.tree.nodes.XValueNodeImpl

interface Any2JsonPrettifyExtensionPoint : Any2JsonRootExtensionPoint

interface Any2JsonCopyExtensionPoint : Any2JsonRootExtensionPoint

interface Any2JsonPreviewExtensionPoint : Any2JsonRootExtensionPoint

interface Any2JsonRootExtensionPoint {
    fun isVisible(event: AnActionEvent): Boolean
    fun isEnabled(event: AnActionEvent): Boolean
    fun process(event: AnActionEvent): Boolean
    fun presentation(event: AnActionEvent): String? = null
}

interface Any2JsonDebuggerExtensionPoint {
    fun loadProperty(node: XValueNodeImpl): Pair<Boolean, Any?>
}

enum class EAction { COPY, REPLACE, PREVIEW }

enum class EType { MAIN, SECONDARY }