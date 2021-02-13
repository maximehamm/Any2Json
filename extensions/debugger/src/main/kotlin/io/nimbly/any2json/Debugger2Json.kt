package io.nimbly.any2json

import com.intellij.debugger.engine.JavaValuePresentation
import com.intellij.debugger.ui.impl.watch.FieldDescriptorImpl
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.xdebugger.impl.ui.tree.XDebuggerTree
import com.intellij.xdebugger.impl.ui.tree.nodes.XValueNodeImpl
import com.sun.jdi.BooleanValue
import com.sun.jdi.ClassType
import com.sun.jdi.DoubleValue
import com.sun.jdi.FloatValue
import com.sun.jdi.IntegerValue
import com.sun.jdi.LongValue
import com.sun.jdi.ObjectReference
import com.sun.jdi.StringReference

class Debugger2Json : Any2JsonExtensionPoint<String> {

    @Suppress("UNCHECKED_CAST")
    override fun build(event: AnActionEvent, actionType: EType) : Pair<String, Map<String, Any>>? {

        val xnode = findXNode(event) ?: return null
        val xname = xnode.name ?: return null
        return xname to xnode.children().toList()
            .filterIsInstance<XValueNodeImpl>()
            .map { it.name to parse(it) }
            .filter { it.second != null }
            .toMap() as Map<String, Any>
    }

    private fun parse(node: XValueNodeImpl): Any? {

        val p = node.valuePresentation
            ?: return null

        if (p is JavaValuePresentation) {

            val method = p.javaClass.declaredFields.find { it.name == "myValueDescriptor" }!!
            method.isAccessible = true
            val value = method.get(p)

            if (value is FieldDescriptorImpl) {

                val value2 = value.value
                    ?: return null

                var any = when (value2) {
                    is BooleanValue -> value2.booleanValue()
                    is IntegerValue -> value2.intValue()
                    is LongValue -> value2.longValue()
                    is DoubleValue -> value2.doubleValue()
                    is FloatValue -> value2.floatValue()
                    is StringReference -> value2.value()
                    else -> null
                }

                if (any == null
                    && value2 is ObjectReference
                    && value.value.type().name().startsWith("java.lang.")) {

                    val t = value.value.type().name().substringAfter("java.lang.")
                    any = when (t) {
                        "Boolean" -> node.rawValue?.toBoolean()
                        "Integer" -> node.rawValue?.toInt()
                        "Long" -> node.rawValue?.toLong()
                        "Double" -> node.rawValue?.toDouble()
                        else -> node.rawValue
                    }
                }

                if (any == null
                    && value2 is ObjectReference
                    && value.value.type() is ClassType
                    && (value.value.type() as ClassType).superclass()?.signature() == "Ljava/lang/Enum;") {
                    any = value.valueText
                }

                if (any != null)
                    return any
            }
        }

        if (node.isLeaf)
            return node.rawValue

        if (node.children.size >0) {

            val toMap = node.children
                .filterIsInstance<XValueNodeImpl>()
                .map { it.name to parse(it) }
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


    override fun isEnabled(event: AnActionEvent, actionType: EType): Boolean {
       return actionType == EType.MAIN && findXNode(event) !=null
    }

    override fun presentation(actionType: EType)
            = "from Variable"
}