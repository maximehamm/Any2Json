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

import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import io.nimbly.any2json.EType.SECONDARY
import io.nimbly.any2json.conversion.looksLikeProperties
import io.nimbly.any2json.conversion.propertiesToMap

const val NO_CONVERSION_FOUND = "Not a valid Json, Xml, Csv, Yaml or Properties !"

fun toJson(any: Any)
    = GsonBuilder()
        .setPrettyPrinting()
        .serializeNulls()
        .disableHtmlEscaping()
        .create()
        .toJson(any)

fun convertToJson(content: String): String {

    val any = content.trimIndent()

    try {
        // json to json
        return toJson(JsonParser.parseString(any.replace("\\\"", "\"")) )
    } catch (ignored: Exception) { }

    try {
        // xml to json
        return toJson(xmlToJson(any).toMap())
    } catch (ignored: Exception) { }

    try {
        // yaml to json
        return toJson(yamlToJson(any))
    } catch (ignored: Exception) { }

    try {
        // csv to json
        if (looksLikeCsv(any))
            return toJson(csvToMap(any))
    } catch (ignored: Exception) { }

    try {
        // properties to json
        if (looksLikeProperties(any))
            return toJson(propertiesToMap(any, SECONDARY))
    } catch (ignored: Exception) { }

    throw Any2JsonConversionException(NO_CONVERSION_FOUND)
}