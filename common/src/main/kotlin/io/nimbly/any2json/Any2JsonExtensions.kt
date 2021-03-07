package io.nimbly.any2json
import com.intellij.openapi.extensions.ExtensionPointName

private const val nameSpace = "io.nimbly.json.Any2Json"

object ANY2JSON {
    operator fun invoke(): ExtensionPointName<Any2JsonExtensionPoint> =
        ExtensionPointName.create("$nameSpace.io.nimbly.json.Any2Json.lang")
}

object DEBUGGER {
    operator fun invoke(): ExtensionPointName<Any2JsonDebuggerExtensionPoint> =
        ExtensionPointName.create("$nameSpace.io.nimbly.json.Any2Json.debugger")
}

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