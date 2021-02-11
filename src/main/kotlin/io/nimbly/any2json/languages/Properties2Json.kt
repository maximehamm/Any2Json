package io.nimbly.any2json.languages

import io.nimbly.any2json.AnyToJsonBuilder
import io.nimbly.any2json.EType
import io.nimbly.any2json.EType.MAIN
import org.apache.commons.lang.StringEscapeUtils.unescapeHtml
import java.util.*

@Suppress("UNCHECKED_CAST")
class Properties2Json(actionType: EType) : AnyToJsonBuilder<String, Any>(actionType) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildMap(content: String): Any {

        val properties = Properties().apply { load(content.byteInputStream()) }
        if (actionType == MAIN) {
            return properties
        }
        else {
            return buildMap( properties.toList().sortedBy { it.first as Comparable<Any> } as List<Pair<String, Any>>)
        }
    }

    private fun buildMap(list: List<Pair<String, Any>>, prefix: String = ""): Any {

        val groups = mutableMapOf<String, Any>()

        var lp: String? = null
        var lg = mutableListOf<Pair<String, Any>>()
        list.forEach {

            var p = it.first.substringAfter(prefix, "").substringBefore(".", "")
            if (p == "") {
                p = it.first.substringAfterLast(".")
                put(p, groups, it.second)
            }
            else if (lp == null) {
                lp = p
                lg.add(it)
            }
            else if (lp != p) {
                put(lp!!, groups, buildMap(lg, "$prefix$lp."))
                lp = p
                lg = mutableListOf()
                lg.add(it)
            }
            else {
                lg.add(it)
            }
        }

        if (lg.isNotEmpty())
            put(lp!!, groups, buildMap(lg, "$prefix$lp."))

        return groups
    }

    private fun put(
        key: String,
        groups: MutableMap<String, Any>,
        value: Any
    ) {
        val temp = groups.get(key)
        if (temp == null) {
            groups[key] = value
        }
        else if (temp is MutableList<*>) {
            (temp as MutableList<Any>).add(value)
        }
        else {
            val l = mutableListOf<Any>()
            l.add(temp)
            l.add(value)
            groups.put(key, l)
        }
    }

    override fun presentation()
        = "from PROPERTIES" + if (actionType == MAIN) " (flat)" else " (hierarchical)"

    override fun isVisible() = true
}
