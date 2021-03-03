package io.nimbly.any2json.languages

import io.nimbly.any2json.AnyToJsonBuilder
import io.nimbly.any2json.EType
import io.nimbly.any2json.csvToMap

class Csv2Json(actionType: EType) : AnyToJsonBuilder<String, List<Map<String, Any>>>(actionType) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildMap(csv: String): List<Map<String, Any>>
        = csvToMap(csv)

    override fun presentation()
        = "from CSV"

    override fun isVisible()
        = actionType == EType.MAIN
}
