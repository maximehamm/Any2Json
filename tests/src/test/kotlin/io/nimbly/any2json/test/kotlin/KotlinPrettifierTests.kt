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

package io.nimbly.any2json.test.kotlin

import io.nimbly.any2json.test.AbstractKotlinTestCase

class KotlinPrettifierTests : AbstractKotlinTestCase() {

    fun testSingleLine() {

        // language=Kt
        configure("""
                package io.nimbly;
                class Test {
                    fun test() {
                        val before = ""${'"'}{ <caret>"id":6, "type": "Something", "revision": 100 }""${'"'}.toString()
                    }
                }""")

        // language=Kt
        assertEquals(prettify(), """
                package io.nimbly;
                class Test {
                    fun test() {
                        val before = ""${'"'}{
                              "id": 6,
                              "type": "Something",
                              "revision": 100
                            }""${'"'}.trimIndent().toString()
                    }
                }""".trimIndent())

        // language=Json
        assertEquals(copy(), """
                {
                  "id": 6,
                  "type": "Something",
                  "revision": 100
                }""".trimIndent())
    }

    fun testMultiLined() {

        // language=Kt
        configure("""
                package io.nimbly;
                class Test {
                    fun test() {
                        val before = ""${'"'}{<caret>
                              "id": 6,
                              "type": "Something",
                              "revision": 100,
                              "history": [  { "lenght": 55, "depth": 77 } ]
                            }""${'"'}
                    }
                }""")

        // language=Kt
        assertEquals(prettify(), """
                package io.nimbly;
                class Test {
                    fun test() {
                        val before = ""${'"'}{
                              "id": 6,
                              "type": "Something",
                              "revision": 100,
                              "history": [
                                {
                                  "lenght": 55,
                                  "depth": 77
                                }
                              ]
                            }""${'"'}.trimIndent()
                    }
                }""".trimIndent())

        // language=Json
        assertEquals(copy(), """
                {
                  "id": 6,
                  "type": "Something",
                  "revision": 100,
                  "history": [
                    {
                      "lenght": 55,
                      "depth": 77
                    }
                  ]
                }""".trimIndent())
    }

    fun testMultiLinedSimpleQuote() {

        // language=Kt
        configure("""
                package io.nimbly;
                class Test {
                    fun test() {
                        val before = "{\n" +
                            "\"id<caret>\": 6,\n" +
                            "\"type\": \"Something\",\n" +
                            "\"revision\": 100,\n" +
                            "\"history\": [ { \"lenght\": 55, \"depth\": 77 } ]\n" +
                            "}"
                    }
                }""")

        // language=Kt
        assertEquals(prettify(), """
                package io.nimbly;
                class Test {
                    fun test() {
                        val before = ""${'"'}{
                              "id": 6,
                              "type": "Something",
                              "revision": 100,
                              "history": [
                                {
                                  "lenght": 55,
                                  "depth": 77
                                }
                              ]
                            }""${'"'}.trimIndent()
                    }
                }""".trimIndent())

        // language=Json
        assertEquals(copy(), """
                {
                  "id": 6,
                  "type": "Something",
                  "revision": 100,
                  "history": [
                    {
                      "lenght": 55,
                      "depth": 77
                    }
                  ]
                }""".trimIndent())
    }

    fun testEmpty() {

        // language=Kt
        configure("""
                package io.nimbly;
                class Test {
                    fun test() {
                        val before = ""${'"'}{<caret>    }""${'"'}
                    }
                }""")

        // language=Kt
        assertEquals(prettify(), """
                package io.nimbly;
                class Test {
                    fun test() {
                        val before = "{}"
                    }
                }""".trimIndent())
    }

    fun testNotEnabled() {

        // language=Kt
        configure("""
                package io.nimbly;
                class Test {
                    fun test() {
                        val before = 999<caret>
                    }
                }""")

        // language=t
        assertEquals(prettify(), "Not enabled")

        // language=t
        assertEquals(copy(), "Not enabled")
    }

    fun testCaretNearQuotes() {

        // language=Kt
        configure("""
                package io.nimbly;
                class Test {
                    fun test() {
                        val before = ""${'"'}{ "id":6, "type": "Something", "revision": 100 }<caret>""${'"'}.toString()
                    }
                }""")

        // language=Json
        assertEquals(copy(), """
                {
                  "id": 6,
                  "type": "Something",
                  "revision": 100
                }""".trimIndent())

        // language=Kt
        assertEquals(prettify(), """
                package io.nimbly;
                class Test {
                    fun test() {
                        val before = ""${'"'}{
                              "id": 6,
                              "type": "Something",
                              "revision": 100
                            }""${'"'}.trimIndent().toString()
                    }
                }""".trimIndent())
    }

}