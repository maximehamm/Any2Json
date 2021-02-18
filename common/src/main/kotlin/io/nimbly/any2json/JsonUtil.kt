package io.nimbly.any2json

import com.google.gson.GsonBuilder
import com.google.gson.JsonParser

fun toJson(any: Any)
    = GsonBuilder()
        .setPrettyPrinting()
        .serializeNulls()
        .disableHtmlEscaping()
        .create()
        .toJson(any)

fun prettify(json: String): String {
    val element = JsonParser.parseString(json)
    return toJson(element)
}