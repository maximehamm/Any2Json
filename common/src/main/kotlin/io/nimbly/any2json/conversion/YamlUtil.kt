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

import io.nimbly.any2json.Any2JsonConversionException
import org.yaml.snakeyaml.Yaml

fun yamlToJson(yaml: String): Any {
    val converted = Yaml().load<Any>(yaml)
    if (converted == null || converted is String)
        throw Any2JsonConversionException("Not yaml ?")
    return converted
}