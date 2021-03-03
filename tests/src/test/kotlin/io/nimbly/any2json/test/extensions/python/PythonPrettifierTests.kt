package io.nimbly.any2json.test.extensions.kotlin

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

    private fun configure(text: String) {
        myFixture.configureByText("test.py", text.trimIndent())
    }
}