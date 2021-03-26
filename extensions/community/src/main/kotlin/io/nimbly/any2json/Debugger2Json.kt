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

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.xdebugger.impl.ui.tree.XDebuggerTree
import com.intellij.xdebugger.impl.ui.tree.nodes.XValueNodeImpl
import io.nimbly.any2json.EAction.COPY
import io.nimbly.any2json.EAction.PREVIEW
import io.nimbly.any2json.util.processAction
import org.jetbrains.debugger.VariableView

class Debugger2JsonGeneratePreview : AbstractDebugger2JsonGenerate(PREVIEW), Any2JsonPreviewExtensionPoint

class Debugger2JsonGenerateCopy : AbstractDebugger2JsonGenerate(COPY), Any2JsonCopyExtensionPoint

abstract class AbstractDebugger2JsonGenerate(private val action: EAction) : Any2JsonRootExtensionPoint {

    override fun process(event: AnActionEvent): Boolean {

        val project = event.project ?: return false
        val xnode = findXNode(event) ?: return false
        val map = xnode.children().toList()
            .filterIsInstance<XValueNodeImpl>()
            .map { it.name to parse(it) }
            .filter { it.second != null }
            .toMap()

        val json = toJson(map)

        return processAction(action, json, project, event.dataContext)
    }

    override fun isEnabled(event: AnActionEvent): Boolean {
        return findXNode(event) !=null
    }

    override fun isVisible(event: AnActionEvent)
        = isEnabled(event)

    override fun presentation(event: AnActionEvent): String? {
        val variable = findXNode(event)?.name ?: return null
        return if (COPY == action)
                "Copy Json sample from variable '$variable'"
            else
                "Preview Json sample from variable '$variable'"
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
}

