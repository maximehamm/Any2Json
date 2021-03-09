package io.nimbly.any2json.test.kotlin

import io.nimbly.any2json.test.AbstractKotlinTestCase

class KotlinArraysTests : AbstractKotlinTestCase() {

    fun testArraysBase() {

        // language=Kt
        configure("""
                package io.nimbly
                import java.util.*
                class Person {
                    private val zeIntegers: Array<Int>? = null
                    private val zeBooleans: List<Boolean>? = null
                    private val zeCharacters: Collection<Char>? = null
                    private val zeStrings: ArrayList<String>? = null
                    private val zeNumbers: Iterator<Number>? = null
                }""")

        // language=Json
        assertEquals(copy(), """
            {
              "zeIntegers": [
                100
              ],
              "zeBooleans": [
                true
              ],
              "zeCharacters": [
                "w"
              ],
              "zeStrings": [
                "Something"
              ],
              "zeNumbers": [
                100
              ]
            }
        """.trimIndent())
    }

    fun testArraysNoType() {

        // language=Kt
        configure("""
            package io.nimbly
            class Person {
                private val zeThings: List<*>? = null
            }""")

        // language=Json
        assertEquals(copy(), """
            {
              "zeThings": []
            }
        """.trimIndent())
    }

}