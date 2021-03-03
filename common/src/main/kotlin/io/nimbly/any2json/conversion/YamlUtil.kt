package io.nimbly.any2json

import org.yaml.snakeyaml.Yaml

fun yamlToJson(yaml: String): List<Map<String, Any>> {
    return Yaml().load(yaml)
}