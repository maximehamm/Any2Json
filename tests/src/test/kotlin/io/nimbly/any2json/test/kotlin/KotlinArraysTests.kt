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