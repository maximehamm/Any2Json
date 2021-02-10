package io.nimbly.any2json.languages

import io.nimbly.any2json.AnyToJsonBuilder
import org.yaml.snakeyaml.Yaml

class Yaml2Json() : AnyToJsonBuilder<String, List<Map<String, Any>>>() {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildMap(content: String, generateValues: Boolean): List<Map<String, Any>> {
        return Yaml().load(content)
    }

    override fun presentation(): String = "from YAML"

    override fun isVisible(generateValues: Boolean) = !generateValues
}
