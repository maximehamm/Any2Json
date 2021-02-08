package io.nimbly.any2json.debugger

import com.intellij.debugger.engine.JavaValuePresentation
import com.intellij.debugger.ui.impl.watch.FieldDescriptorImpl
import com.intellij.xdebugger.impl.ui.tree.nodes.XValueNodeImpl
import com.jetbrains.jdi.BooleanValueImpl
import com.jetbrains.jdi.ClassTypeImpl
import com.jetbrains.jdi.DoubleValueImpl
import com.jetbrains.jdi.FloatValueImpl
import com.jetbrains.jdi.IntegerValueImpl
import com.jetbrains.jdi.LongValueImpl
import com.jetbrains.jdi.ObjectReferenceImpl
import com.jetbrains.jdi.StringReferenceImpl
import io.nimbly.any2json.AnyToJsonBuilder
import org.jetbrains.kotlin.idea.debugger.isSubtype

class Variable2Json() : AnyToJsonBuilder<XValueNodeImpl>()  {

    @Suppress("UNCHECKED_CAST")
    override fun buildMap(type: XValueNodeImpl, generateValues: Boolean): Map<String, Any> {

        return type.children().toList()
            .filter { it is XValueNodeImpl }
            .map { it as XValueNodeImpl }
            .map { it.name to parse(it, generateValues) }
            .filter { it.second != null }
            .toMap() as Map<String, Any>
    }

    private fun parse(node: XValueNodeImpl, generateValues: Boolean): Any? {

        if (node.valuePresentation is JavaValuePresentation) {

            val method = (node.valuePresentation as JavaValuePresentation).javaClass.declaredFields.find { it.name == "myValueDescriptor" }!!
            method.isAccessible = true
            val value = method.get(node.valuePresentation)

            if (value is FieldDescriptorImpl) {

                val value2 = value.value
                    ?: return null

                var any = when (value2) {
                    is BooleanValueImpl -> value2.booleanValue()
                    is IntegerValueImpl -> value2.intValue()
                    is LongValueImpl -> value2.longValue()
                    is DoubleValueImpl -> value2.doubleValue()
                    is FloatValueImpl -> value2.floatValue()
                    is StringReferenceImpl -> value2.value()
                    else -> null
                }

                value.getValue()

                if (any == null
                    && value2 is ObjectReferenceImpl
                    && value.getValue().type().name().startsWith("java.lang.")) {

                    val t = value.getValue().type().name().substringAfter("java.lang.")
                    any = when {
                        t == "Boolean" -> node.rawValue?.toBoolean()
                        t == "Integer" -> node.rawValue?.toInt()
                        t == "Long" -> node.rawValue?.toLong()
                        else -> node.rawValue
                    }
                }

                if (any == null
                    && value2 is ObjectReferenceImpl
                    && value.getValue().type() is ClassTypeImpl
                    && (value.getValue().type() as ClassTypeImpl).superclass()?.signature() == "Ljava/lang/Enum;") {
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
                .map { it.name to parse(it, generateValues) }
                .toMap()

            return toMap
        }

        if (node.rawValue !=null)
            return node.rawValue

        return null
    }

    override fun presentation() = "from Variable"
}