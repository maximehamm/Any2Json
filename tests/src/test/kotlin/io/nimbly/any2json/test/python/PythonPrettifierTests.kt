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

package io.nimbly.any2json.test.python

import io.nimbly.any2json.test.AbstractTestCase

class PythonPrettifierTests : AbstractTestCase() {

    fun testSingleQuote() {

        // language=t
        configure("""
                phrase1 = ""${'"'}<caret>{"a":1,"b":2,"c":3, "x" : {"d":4, "e":5} }""${'"'}
                """)

        // language=t
        assertEquals(prettify(), """
                phrase1 = ""${'"'}{
                  "a": 1,
                  "b": 2,
                  "c": 3,
                  "x": {
                    "d": 4,
                    "e": 5
                  }
                }""${'"'}
                """.trimIndent())
    }

    fun testDoubleQuote() {

        // language=t
        configure("""
                phrase2 = "<caret>{\"a\":1,\"b\":2,\"c\":3, \"x\" : {\"d\":4, \"e\":5} }"
                """)

        // language=t
        assertEquals(prettify(), """
                phrase2 = ""${'"'}{
                  "a": 1,
                  "b": 2,
                  "c": 3,
                  "x": {
                    "d": 4,
                    "e": 5
                  }
                }""${'"'}
                """.trimIndent())

        // language=Json
        assertEquals(copy(), """
                {
                  "a": 1,
                  "b": 2,
                  "c": 3,
                  "x": {
                    "d": 4,
                    "e": 5
                  }
                }""".trimIndent())
    }

    fun testMultilinesWithinParenthesis() {

        // language=t
        configure("""
                phrase3 = ("{<caret>\n"
                           "\"a\": 1,\n"
                           "\"b\": 2,\n"
                           "\"c\": 3,\n"
                           "\"x\": {\n"
                           "\"d\": 4,\n"
                           "\"e\": 5\n"
                           "}\n"
                           "}")
                """)

        // language=t
        assertEquals(prettify(), """
                phrase3 = ""${'"'}{
                  "a": 1,
                  "b": 2,
                  "c": 3,
                  "x": {
                    "d": 4,
                    "e": 5
                  }
                }""${'"'}
                """.trimIndent())

        // language=Json
        assertEquals(copy(), """
                {
                  "a": 1,
                  "b": 2,
                  "c": 3,
                  "x": {
                    "d": 4,
                    "e": 5
                  }
                }""".trimIndent())
    }

    fun testNotEnabled() {

        // language=t
        configure("""
                num = 999<caret>
                """)

        // language=t
        assertEquals(prettify(), "Not enabled")

        // language=t
        assertEquals(copy(), "Not enabled")
    }

    private fun configure(text: String) {
        myFixture.configureByText("test.py", text.trimIndent())
    }
}