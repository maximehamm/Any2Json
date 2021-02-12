package io.nimbly.any2json
import com.intellij.openapi.extensions.ExtensionPointName

object ANY2JSON {

    private const val nameSpace = "io.nimbly.json.Any2Json"
    private const val name = "io.nimbly.json.Any2Json.lang"
    private const val fullyQualifiedName = "$nameSpace.$name"

    operator fun invoke(): ExtensionPointName<Any2JsonExtensionPoint<Any>> =
        ExtensionPointName.create(fullyQualifiedName)
}