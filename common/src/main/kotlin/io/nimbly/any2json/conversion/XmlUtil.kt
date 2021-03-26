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

import org.json.JSONException
import org.json.JSONObject
import org.json.XML

fun xmlToJson(xml: String): JSONObject {
    val json = XML.toJSONObject(xml)
    if (json.toMap().isEmpty() && xml.isNotEmpty())
        throw JSONException("Xml to Json fails")
    return json
}