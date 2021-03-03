package io.nimbly.any2json

import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import org.json.JSONException
import org.json.JSONObject
import org.json.XML

fun toJson(any: Any)
    = GsonBuilder()
        .setPrettyPrinting()
        .serializeNulls()
        .disableHtmlEscaping()
        .create()
        .toJson(any)

fun xmlToJson(xml: String): JSONObject {
    val json = XML.toJSONObject(xml)
    if (json.toMap().isEmpty() && xml.isNotEmpty())
        throw JSONException("Xml to Json fails")
    return json
}

fun convertToPrettifiedJson(any: String): String {

    try {
        // json to json
        return toJson(JsonParser.parseString(any.replace("\\\"", "\"")) )
    } catch (ignored: Exception) { }

    try {
        // xml to json
        return  toJson(xmlToJson(any).toMap())
    } catch (ignored: Exception) { }

    throw Any2JsonConversionException("Not a valid Json or Xml !")
}