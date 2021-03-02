package io.nimbly.any2json

import com.google.gson.JsonSyntaxException
import com.google.gson.stream.MalformedJsonException
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.extensions.ExtensionPointName
import io.nimbly.any2json.util.warn

class Any2JsonPrettifyAction : Any2JsonRootAction<Any2JsonPrettifyExtensionPoint>(PRETTIFY())

class Any2JsonCopyAction : Any2JsonRootAction<Any2JsonCopyExtensionPoint>(COPY())

open class Any2JsonRootAction<T:Any2JsonRootExtensionPoint>(
    private val EXT: ExtensionPointName<T>) : AnAction() {

    override fun actionPerformed(event: AnActionEvent) {
        try {
            EXT.extensionList.find {
                it.prettify(event)
            }
        }
        catch (e: MalformedJsonException) {
            warn("Malformed Json !", event.project!!)
        }
        catch (e: JsonSyntaxException) {
            warn("Malformed Json !", event.project!!)
        }
        catch (e: Exception) {
            warn("Json prettifier error : ${e.message}", event.project!!)
        }
    }

    override fun update(event: AnActionEvent) {
        val enabled =
            EXT.extensionList.find {
                it.isEnabled(event)
            } != null
        event.presentation.isVisible = enabled
        event.presentation.isEnabled = enabled
    }
}