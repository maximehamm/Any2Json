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

import com.google.gson.JsonSyntaxException
import com.google.gson.stream.MalformedJsonException
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.extensions.ExtensionPointName
import io.nimbly.any2json.util.warn

class Any2JsonPrettifyAction : Any2JsonRootAction<Any2JsonPrettifyExtensionPoint>(PRETTIFY())

class Any2JsonCopyAction : Any2JsonRootAction<Any2JsonCopyExtensionPoint>(COPY())

class Any2JsonPreviewAction : Any2JsonRootAction<Any2JsonPreviewExtensionPoint>(PREVIEW())

open class Any2JsonRootAction<T:Any2JsonRootExtensionPoint>(
    private val EXT: ExtensionPointName<T>) : AnAction() {

    override fun actionPerformed(event: AnActionEvent) {
        try {
            EXT.extensionList.find {
                it.process(event)
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

        ext?.presentation(event).let { if (it!=null) event.presentation.text = it }
        event.presentation.isVisible = ext != null
        event.presentation.isEnabled = ext != null && ext.isEnabled(event)
    }
}