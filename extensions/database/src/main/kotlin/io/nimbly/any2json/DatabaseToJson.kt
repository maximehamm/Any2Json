package io.nimbly.any2json

import com.intellij.database.dialects.postgres.model.PgLocalTableColumn
import com.intellij.database.model.DasTable
import com.intellij.database.model.ObjectKind
import com.intellij.database.psi.DbElement
import com.intellij.database.psi.DbTable
import com.intellij.database.run.ui.table.TableResultView
import com.intellij.database.util.DbImplUtil
import com.intellij.database.view.DatabaseStructure
import com.intellij.database.view.DatabaseStructure.FamilyGroup
import com.intellij.database.view.DatabaseView
import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.project.Project
import com.intellij.util.containers.JBIterable

class DatabaseToJson : Any2JsonExtensionPoint {

    @Suppress("UNCHECKED_CAST")
    override fun build(event: AnActionEvent, actionType: EType) : Pair<String, Any>? {

        val table = getTable(event)
        val result = getTableResult(event)

        if (result != null) {

            val list = mutableListOf<Map<String, Any?>>()
            val headers = result.columnModel.columns.toList().map { it.headerValue.toString() }

            for (row in 0 until result.model.rowCount) {
                val map = mutableMapOf<String, Any?>()
                for (column in 0 until headers.count()) {
                    map[headers[column]] = result.model.getValueAt(row, column)
                }
                list.add(map)
            }

            return "result" to list
        }
        else if (table != null) {
            val columns = table.getDasChildren(ObjectKind.COLUMN).toList().map { it as PgLocalTableColumn }
            return table.name to columns
                .map { it.name to parse(it, actionType) }
                .toMap()
        }

        return null
    }

    private fun getTableResult(event: AnActionEvent): TableResultView? {
        val data = event.getData(PlatformDataKeys.CONTEXT_COMPONENT)
            ?: return null

        if (data !is TableResultView)
            return null

        return data
    }

    @Suppress("NAME_SHADOWING")
    private fun parse(
        column: PgLocalTableColumn,
        actionType: EType
    ): Any {

        val typeName = column.dataType.typeName
        GENERATORS[typeName]?.let {
            return it.generate(actionType == EType.SECONDARY, null)
        }

        return GString().generate(actionType == EType.SECONDARY, null)
    }

    override fun isEnabled(event: AnActionEvent, actionType: EType): Boolean {

        val result = getTableResult(event)
        if (result !=null)
            return actionType == EType.MAIN

        return getTable(event) != null
    }

    override fun presentation(actionType: EType, event: AnActionEvent): String {
        val result = getTableResult(event)
        if (result !=null)
            return "from Results"
        return "from Table" + if (actionType == EType.SECONDARY) " with Data" else ""
    }

    companion object {
        val GENERATORS = mapOf(
            "varchar" to GString(), "text" to GString(),
            "char" to GChar(),
            "uuid" to GUUID(),
            "bigint" to GLong(), "float4" to GLong(),  "float8" to GLong(),
            "integer" to GInteger(),
            "boolean" to GBoolean(), "bit" to GBoolean(), "bool" to GBoolean(),
            "timestamp" to GObject(), "date" to GObject(), "time" to GObject()
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
            iter = DatabaseView.getSelectedElements(dataContext,
                { o: DatabaseStructure.Group? ->
                    o is FamilyGroup && DbImplUtil.isDataTable(
                        o.childrenKind )
                }).unique()
        }
        return iter
    }
}