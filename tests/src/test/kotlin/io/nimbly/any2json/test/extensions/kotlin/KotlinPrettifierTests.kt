package io.nimbly.any2json.test.extensions.kotlin

import io.nimbly.any2json.test.extensions.AbstractKotlinTestCase

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
}