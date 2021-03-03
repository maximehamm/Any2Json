package io.nimbly.any2json

import com.google.gson.GsonBuilder
import com.google.gson.JsonParser

val NO_CONVERSION_FOUND = "Not a valid Json, Xml, Csv or Yaml !"

fun toJson(any: Any)
    = GsonBuilder()
        .setPrettyPrinting()
        .serializeNulls()
        .disableHtmlEscaping()
        .create()
        .toJson(any)

fun convertToJson(any: String): String {

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
        return toJson(yamlToJson(any.trimIndent()))
    } catch (ignored: Exception) { }

    try {
        // csv to json
        if (looksLikeCsv(any))
            return toJson(csvToMap(any))
    } catch (ignored: Exception) { }


    throw Any2JsonConversionException(NO_CONVERSION_FOUND)
}