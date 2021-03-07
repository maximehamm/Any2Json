package io.nimbly.any2json

import com.google.gson.JsonSyntaxException
import com.google.gson.stream.MalformedJsonException
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.extensions.ExtensionPointName

class Any2JsonPrettifyAction : Any2JsonRootAction<Any2JsonPrettifyExtensionPoint>(PRETTIFY())

class Any2JsonCopyAction : Any2JsonRootAction<Any2JsonCopyExtensionPoint>(COPY())

class Any2JsonPreviewAction : Any2JsonRootAction<Any2JsonPreviewExtensionPoint>(PREVIEW())

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
        catch (e: Any2JsonConversionException) {
            warn(e.message!!, event.project!!)
        }
        catch (e: Exception) {
            warn("Json prettifier error : ${e.message}", event.project!!)
        }
    }

    override fun update(event: AnActionEvent) {

        val ext = EXT.extensionList.find {
            it.isVisible(event)
        }

        event.presentation.isVisible = ext != null
        event.presentation.isEnabled = ext != null && ext.isEnabled(event)
    }
}