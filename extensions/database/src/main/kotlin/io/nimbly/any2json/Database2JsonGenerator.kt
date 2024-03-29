/*
 * ANY2JSON
 * Copyright (C) 2024  Maxime HAMM - NIMBLY CONSULTING - maxime.hamm.pro@gmail.com
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

import com.intellij.database.model.DasObject
import com.intellij.database.model.DasTable
import com.intellij.database.model.DasTypedObject
import com.intellij.database.model.ObjectKind
import com.intellij.database.psi.DbElement
import com.intellij.database.psi.DbTable
import com.intellij.database.run.ui.table.TableResultRowHeader
import com.intellij.database.run.ui.table.TableResultView
import com.intellij.database.util.DbImplUtil
import com.intellij.database.view.DatabaseStructure
import com.intellij.database.view.DatabaseView
import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.project.Project
import com.intellij.util.containers.JBIterable
import io.nimbly.any2json.EAction.COPY
import io.nimbly.any2json.EAction.PREVIEW
import io.nimbly.any2json.conversion.toJson
import io.nimbly.any2json.util.processAction

class Database2JsonGeneratePreview : AbstractDatabase2JsonGenerate(PREVIEW), Any2JsonPreviewExtensionPoint

class Database2JsonGenerateCopy : AbstractDatabase2JsonGenerate(COPY), Any2JsonCopyExtensionPoint

abstract class AbstractDatabase2JsonGenerate(private val action: EAction) : Any2JsonRootExtensionPoint {

    override fun process(event: AnActionEvent): Boolean {

        val table = getTable(event)
        val result = getTableResult(event)
        val project = event.project ?: return false

        val json =  when {
            result != null -> {
                var selected = result.selectedRows
                if (selected.isEmpty()
                    || selected.size == 1 && result.selectedColumnCount==1)
                    selected =  (0 until result.model.rowCount).toList().toIntArray()

                val list = mutableListOf<Map<String, Any?>>()
                val headers = result.columnModel.columns.toList().map { it.headerValue.toString() }
                for (row in selected) {

                    val map = mutableMapOf<String, Any?>()
                    for (column in 0 until headers.count()) {
                        map[headers[column]] = result.model.getValueAt(row, column)
                    }
                    list.add(map)
                }
                toJson(list)
            }
            table != null -> {
                val columns = table.getDasChildren(ObjectKind.COLUMN).toList()
                val map = columns
                    .map { it.name to parse(it) }
                    .toMap()
                toJson(map)
            }
            else -> {
                return false
            }
        }

        return processAction(action, json, project, event.dataContext)
    }

    @Suppress("NAME_SHADOWING")
    private fun parse(
        column: DasObject,
        actionType: EType = EType.SECONDARY
    ): Any {

        val typeName = if (column is DasTypedObject) column.dataType.typeName else ""
        GENERATORS[typeName]?.let {
            return it.generate(actionType == EType.SECONDARY, null)
        }

        return GString().generate(actionType == EType.SECONDARY, null)
    }

    override fun isEnabled(event: AnActionEvent): Boolean {
        val result = getTableResult(event)
        if (result != null)
            return true
        return event.place == "DatabaseViewPopup"
            && getTable(event) != null
    }

    override fun isVisible(event: AnActionEvent)
        = isEnabled(event)

    override fun presentation(event: AnActionEvent): String {
        val from = if (getTableResult(event) != null) "results" else "table"
        return if (COPY == action)
                "Copy Json sample from $from"
            else
                "Preview Json sample from $from"
    }

    private fun getTableResult(event: AnActionEvent): TableResultView? {
        val data = event.getData(PlatformDataKeys.CONTEXT_COMPONENT)
            ?: return null
        if (data is TableResultView)
            return data
        if (data is TableResultRowHeader && data.table is TableResultView)
            return data.table as TableResultView
        return null
    }

    companion object {
        val GENERATORS = mapOf(
            "varchar" to GString(), "text" to GString(),
            "char" to GChar(),
            "uuid" to GUUID(), "varbinary" to GUUID(),
            "bigint" to GLong(), "float4" to GLong(),  "float8" to GLong(),
            "integer" to GInteger(),
            "boolean" to GBoolean(), "bit" to GBoolean(), "bool" to GBoolean(),
            "timestamp" to GObject(), "date" to GObject(), "datetime" to GObject(), "time" to GObject()
        )
    }

    private fun getTable(event: AnActionEvent) : DasTable? {

        val project = event.project
            ?: return null

        val selectedElements = try {
            getSelectedElements(project)
        } catch (e: Exception) {
            return null
        }

        if (selectedElements.isEmpty)
            return null

        val el = selectedElements[0]
        if (el !is DbTable)
            return null

        val delegate = el.delegate
        if (delegate !is DasTable)
            return null

        return delegate
    }

    private fun getSelectedElements(project: Project): JBIterable<DbElement> {
        val iter: JBIterable<DbElement>
        val view = DatabaseView.getDatabaseView(project)
        if (view == null) {
            iter = JBIterable.empty()
        } else {
            val dataContext = DataManager.getInstance().getDataContext(view)
            iter = DatabaseView.getSelectedElements(dataContext) { o ->
                o is DatabaseStructure.FamilyGroup && DbImplUtil.isDataTable(
                    o.childrenKind
                )
            }.unique()
        }
        return iter
    }

}

