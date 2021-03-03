package io.nimbly.any2json.languages

import io.nimbly.any2json.AnyToJsonBuilder
import io.nimbly.any2json.EType
import io.nimbly.any2json.EType.MAIN
import io.nimbly.any2json.conversion.propertiesToMap
import java.util.*

@Suppress("UNCHECKED_CAST")
class Properties2Json(actionType: EType) : AnyToJsonBuilder<String, Any>(actionType) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildMap(content: String)
        = propertiesToMap(content, actionType)

    override fun presentation()
        = "from PROPERTIES" + if (actionType == MAIN) " (flat)" else " (hierarchical)"

    override fun isVisible() = true
}
