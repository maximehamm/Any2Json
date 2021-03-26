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