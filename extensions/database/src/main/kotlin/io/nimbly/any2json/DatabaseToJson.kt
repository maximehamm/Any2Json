package io.nimbly.any2json

import com.intellij.database.dialects.base.introspector.jdbc.wrappers.DatabaseMetaDataWrapper
import com.intellij.database.dialects.base.introspector.jdbc.wrappers.DatabaseMetaDataWrapper.TableColumn
import com.intellij.database.dialects.postgres.model.PgLocalTableColumn
import com.intellij.database.model.DasTable
import com.intellij.database.model.ObjectKind
import com.intellij.database.psi.DbElement
import com.intellij.database.psi.DbTable
import com.intellij.database.psi.DbTableImpl
import com.intellij.database.util.DbImplUtil
import com.intellij.database.view.DatabaseStructure
import com.intellij.database.view.DatabaseStructure.FamilyGroup
import com.intellij.database.view.DatabaseView
import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.util.containers.JBIterable

class DatabaseToJson : Any2JsonExtensionPoint {

    @Suppress("UNCHECKED_CAST")
    override fun build(event: AnActionEvent, actionType: EType) : Pair<String, Map<String, Any>>? {

        val table = getTable(event)
            ?: return null

        val columns = table.getDasChildren(ObjectKind.COLUMN).toList().map { it as PgLocalTableColumn }
        //columns[3].dataType.typeName
        return table.name to columns
            .map { it.name to parse( it, actionType) }
            .toMap()
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
        return getTable(event) != null
    }

    override fun presentation(actionType: EType)
            = "from Table" + if (actionType == EType.SECONDARY) " with Data" else ""

    companion object {
        val GENERATORS = mapOf(
            "varchar" to GString(), "text" to GString(),
            "uuid" to GUUID(),
            "bigint" to GLong(),
            "integer" to GInteger(),
            "boolean" to GBoolean(),
            "timestamp" to GObject()
        )
    }

    private fun getTable(event: AnActionEvent) : DasTable? {

        val project = event.project
            ?: return null

        val selectedElements = getSelectedElements(project)
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

    private fun getSelectedElements(project: Project?): JBIterable<DbElement> {
        val iter: JBIterable<DbElement>
        if (project == null) {
            iter = JBIterable.empty()
        } else {
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
        }
        return iter
    }
}