/*
 * ANY2JSON
 * Copyright (C) 2021  Maxime HAMM - NIMBLY CONSULTING - maxime.hamm.pro@gmail.com
 *
 * This document is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This work is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package io.nimbly.any2json

import com.intellij.debugger.engine.JavaValuePresentation
import com.intellij.debugger.ui.impl.watch.FieldDescriptorImpl
import com.intellij.xdebugger.impl.ui.tree.nodes.XValueNodeImpl
import com.sun.jdi.*

class JavaDebugger2Json : Any2JsonDebuggerExtensionPoint {

    /**
     * If left is "true" then caller should return right
     */
    override fun loadProperty(node: XValueNodeImpl): Pair<Boolean, Any?> {

        val p = node.valuePresentation
        if (p !is JavaValuePresentation)
            return false to null

        val method = p.javaClass.declaredFields.find { it.name == "myValueDescriptor" }!!
        method.isAccessible = true
        val value = method.get(p)

        if (value !is FieldDescriptorImpl)
            return false to null

        val value2 = value.value
            ?: return true to null

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
            && value.value.type().name().startsWith("java.lang.")
        ) {

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
            && (value.value.type() as ClassType).superclass()?.signature() == "Ljava/lang/Enum;"
        ) {
            any = value.valueText
        }

        if (any != null)
            return true to any

        return false to null
    }
}