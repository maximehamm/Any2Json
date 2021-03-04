package io.nimbly.any2json

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import io.nimbly.any2json.EType.MAIN
import io.nimbly.any2json.EType.SECONDARY
import io.nimbly.any2json.debugger.Debugger2Json
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

class Any2JsonDefaultAction : Any2JsonAction(MAIN)

class Any2JsonRandomAction : Any2JsonAction(SECONDARY)

abstract class Any2JsonAction(private val actionType: EType): AnAction() { //DebuggerAction()

    override fun actionPerformed(event: AnActionEvent) {

        val project = event.project!!

        try {

            // Find extension
            var result: Pair<String, Any>? = null
            ANY2JSON().extensionList.find {
                result = it.build(event, actionType)
                result != null
            }

            // Try debugger
            if (result == null) {
                result = Debugger2Json().build(event)
            }

            // Oups !?
            if (result == null)
                throw Any2PojoException("Unable to define context !")

            // Convert to Json
            val json = toJson(result!!.second)

            // Put to clipboard
            Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(json), StringSelection(json))

            // Report notificagton
            info("${result!!.first} to Json copied to clipboard !", project)

        } catch (ex: Any2PojoException) {
            warn(ex.message!!, project)
        } catch (ex: Exception) {
            ex.printStackTrace()
            error("Any to Json error !", project)
        }
    }

    override fun update(event: AnActionEvent) {

        if (Debugger2Json().isVisible(event, actionType)) {
            event.presentation.text = "Generate Json"
            event.presentation.isVisible = true
            event.presentation.isEnabled = true
            return
        }

        var enabledByExtension: String? = null
        ANY2JSON().extensionList.find { ext ->
            if (ext.isEnabled(event, actionType)) {
                enabledByExtension = ext.presentation(actionType, event)
                true
            } else {
                false
            }
        }

        if (enabledByExtension != null) {
            event.presentation.text = "Generate Json $enabledByExtension"
            event.presentation.isVisible = true
            event.presentation.isEnabled = true
        }
        else {
            event.presentation.isVisible = false
            event.presentation.isEnabled = false
        }
    }
}