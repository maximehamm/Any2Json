package io.nimbly.any2json

import org.json.JSONException
import org.json.JSONObject
import org.json.XML

fun xmlToJson(xml: String): JSONObject {
    val json = XML.toJSONObject(xml)
    if (json.toMap().isEmpty() && xml.isNotEmpty())
        throw JSONException("Xml to Json fails")
    return json
}