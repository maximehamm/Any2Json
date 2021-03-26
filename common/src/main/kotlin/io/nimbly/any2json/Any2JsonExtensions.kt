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
import com.intellij.openapi.extensions.ExtensionPointName

private const val nameSpace = "io.nimbly.json.Any2Json"

object PRETTIFY {
    operator fun invoke(): ExtensionPointName<Any2JsonPrettifyExtensionPoint> =
        ExtensionPointName.create("$nameSpace.io.nimbly.json.Any2Json.prettify")
}

object COPY {
    operator fun invoke(): ExtensionPointName<Any2JsonCopyExtensionPoint> =
        ExtensionPointName.create("$nameSpace.io.nimbly.json.Any2Json.copy")
}

object PREVIEW {
    operator fun invoke(): ExtensionPointName<Any2JsonPreviewExtensionPoint> =
        ExtensionPointName.create("$nameSpace.io.nimbly.json.Any2Json.preview")
}

object DEBUGGER {
    operator fun invoke(): ExtensionPointName<Any2JsonDebuggerExtensionPoint> =
        ExtensionPointName.create("$nameSpace.io.nimbly.json.Any2Json.debugger")
}