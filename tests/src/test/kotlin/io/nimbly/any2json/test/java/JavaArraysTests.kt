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

package io.nimbly.any2json.test.java

import io.nimbly.any2json.test.AbstractJavaTestCase

class JavaArraysTests : AbstractJavaTestCase() {

    fun testArraysBase() {

        // language=Java
        configure("""
                package io.nimbly;
                import java.util.List;
                import java.util.Iterator;
                import java.util.Collection;
                public class Person {
                    private Integer[] zeIntegers;
                    private List<Boolean> zeBooleans;
                    private Collection<Character> zeCharacters;
                    private java.util.ArrayList<String> zeStrings;
                    private Iterator<Number> zeNumbers;
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

        // language=Java
        configure("""
                package io.nimbly;
                public class Person {
                    private java.util.List zeThings;
                }""")

        // language=Json
        assertEquals(copy(), """
            {
              "zeThings": []
            }
        """.trimIndent())
    }

}