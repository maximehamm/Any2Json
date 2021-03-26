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

class JavaBaseTests : AbstractJavaTestCase() {

    fun testIgnoreStatic() {

        // language=Java
        configure("""
            package io.nimbly;
            public class Person {
                public static final String TEST = "TEST";
                private int name;
            }""")

        // language=Json
        assertEquals(copy(), """
            {
              "name": 100
            }""".trimIndent())
    }

    fun testInterface() {

        // language=Java
        configure("""
            package io.nimbly;
            interface ISchool {
                Long id = 123456789l;
            }""")

        // language=Json
        assertEquals(copy(), """
            {
              "id": 123456789
            }
        """.trimIndent())
    }
}