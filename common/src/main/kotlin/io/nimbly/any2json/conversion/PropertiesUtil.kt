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

package io.nimbly.any2json.conversion

import io.nimbly.any2json.EType
import io.nimbly.any2json.EType.MAIN
import java.util.*

fun looksLikeProperties(content: String): Boolean {

    val eq = content.count { it == '=' }
    val cr = content.count { it == '\n' }

    return eq == 1 && cr == 0
        ||  cr>0 && 1.0 * eq / cr > 0.8
}

@Suppress("UNCHECKED_CAST")
fun propertiesToMap(content: String, actionType: EType): Any {

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

@Suppress("UNCHECKED_CAST")
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