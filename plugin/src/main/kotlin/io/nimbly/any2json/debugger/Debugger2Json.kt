package io.nimbly.any2json.debugger

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.xdebugger.impl.ui.tree.XDebuggerTree
import com.intellij.xdebugger.impl.ui.tree.nodes.XValueNodeImpl
import io.nimbly.any2json.DEBUGGER
import io.nimbly.any2json.EType
import org.jetbrains.debugger.VariableView

open class Debugger2Json {

    @Suppress("UNCHECKED_CAST")
    fun build(event: AnActionEvent) : Pair<String, Map<String, Any>>? {

        var xnode = findXNode(event) ?: return null
        val xname = xnode.name ?: return null

        xnode = findXNode(event) ?: return null

        return xname to xnode.children().toList()
            .filterIsInstance<XValueNodeImpl>()
            .map { it.name to parse(it) }
            .filter { it.second != null }
            .toMap() as Map<String, Any>
    }

    private fun parse(node: XValueNodeImpl, level: Int = 0): Any? {

        if (node.name == "__proto__")
            return null

        if (level<3)
            node.valueContainer.calculateEvaluationExpression().blockingGet(200)

        val modifier = node.valueContainer
        if (modifier is VariableView) {

            val vv = modifier.getValue()?.type?.name
            if (vv != null) {
                when (vv) {
                    "STRING" -> return node.rawValue
                    "BOOLEAN" -> return node.rawValue?.toBoolean()
                    "NUMBER" -> return node.rawValue?.toInt()
                    "BIGINT" -> return node.rawValue?.toLong()
                    "NULL" -> return null
                }
            }
        }

        // Try using specifique implementation
        DEBUGGER().extensionList.forEach {
            val (found, any) = it.loadProperty(node)
            if (found)
                return any
        }

        if (node.children.size >0) {

            val toMap = node.children
                .filterIsInstance<XValueNodeImpl>()
                .map { it.name to parse(it, level+1) }
                .toMap()

            var isList = true
            toMap.keys.filterNotNull()
                .forEachIndexed { index, k ->
                    if (k.toIntOrNull() != index) isList = false }
            if (isList) {
                return toMap.values.filterNotNull()
            }
            else {
                return toMap
            }
        }

        if (node.rawValue !=null)
            return node.rawValue

        return null
    }

    private fun findXNode(event: AnActionEvent) : XValueNodeImpl? {
        val xtree: XDebuggerTree = event.dataContext.getData("xdebugger.tree") as XDebuggerTree?
            ?: return null
        val xpath = xtree.selectionPath?.lastPathComponent
        if (xpath !is XValueNodeImpl)
            return null
        if (xpath.valuePresentation == null)
            return null
        if (xpath.name == null)
            return null
        return xpath
    }

    fun isVisible(event: AnActionEvent, actionType: EType)
        = actionType == EType.MAIN && findXNode(event) !=null

}