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

class JavaPrettifierTests : AbstractJavaTestCase() {

    fun testSingleLine() {

        // language=Java
        configure("""
                package io.nimbly;
                public class Test {
                    public void test() {
                        String before = "<caret>{ \"id\":6, \"type\": \"Something\", \"revision\": 100 }";
                    }
                }""")

        // language=Java
        assertEquals(prettify(), """
                package io.nimbly;
                public class Test {
                    public void test() {
                        String before = "{\n" +
                                "  \"id\": 6,\n" +
                                "  \"type\": \"Something\",\n" +
                                "  \"revision\": 100\n" +
                                "}";
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

        // language=Java
        configure("""
                package io.nimbly;
                public class Test {
                    public void test() {
                        String before = "{<caret>\n" +
                                "\"id\":6,\n" +
                                "\"type\": \"Something\",\n" +
                                "\"revision\": 100,\n" +
                                "\"history\": [ { \"lenght\": 55 } ]\n" +
                                "}\n";
                    }
                }""")

        // language=Java
        assertEquals(prettify(), """
                package io.nimbly;
                public class Test {
                    public void test() {
                        String before = "{\n" +
                                "  \"id\": 6,\n" +
                                "  \"type\": \"Something\",\n" +
                                "  \"revision\": 100,\n" +
                                "  \"history\": [\n" +
                                "    {\n" +
                                "      \"lenght\": 55\n" +
                                "    }\n" +
                                "  ]\n" +
                                "}";
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
                      "lenght": 55
                    }
                  ]
                }""".trimIndent())
    }

    fun testEmpty() {

        // language=Java
        configure("""
                package io.nimbly;
                public class Test {
                    public void test() {
                        String before = "{<caret>   }";
                    }
                }""")

        // language=Java
        assertEquals(prettify(), """
                package io.nimbly;
                public class Test {
                    public void test() {
                        String before = "{}";
                    }
                }""".trimIndent())

        // language=Json
        assertEquals(copy(), """
                {}""".trimIndent())
    }

    fun testNotEnabled() {

        // language=Java
        configure("""
                package io.nimbly;
                public class Test {
                    public void test() {
                        String before = 999<caret>;
                    }
                }""")

        // language=t
        assertEquals(prettify(), "Not enabled")

        // language=t
        assertEquals(copy(), "Not enabled")
    }
}