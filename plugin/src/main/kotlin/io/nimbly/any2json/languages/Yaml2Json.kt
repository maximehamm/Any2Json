package io.nimbly.any2json.languages

import io.nimbly.any2json.AnyToJsonBuilder
import io.nimbly.any2json.EType
import org.yaml.snakeyaml.Yaml

class Yaml2Json(actionType: EType) : AnyToJsonBuilder<String, List<Map<String, Any>>>(actionType) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildMap(content: String): List<Map<String, Any>> {
        return Yaml().load(content)
    }

    override fun presentation()
        = "from YAML"

    override fun isVisible()
        = actionType == EType.MAIN
}