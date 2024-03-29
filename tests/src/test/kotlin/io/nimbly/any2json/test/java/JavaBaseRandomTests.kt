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

class JavaBaseRandomTests : AbstractJavaTestCase() {

    fun testPrimitives() {
        configure(
            """
                package io.nimbly;
                public class Person {
                    private boolean zeBoolean;
                    private int zeInt;
                    private long zeLong;
                    private double zeDouble;
                    private float zeFloat;
                    private char zeChar;
                }"""
        )

        assertEquals(copy(), """
            {
              "zeBoolean": true,
              "zeInt": 100,
              "zeLong": 100,
              "zeDouble": 9999900000.0,
              "zeFloat": 9999900000.000000,
              "zeChar": "w"
            }
        """.trimIndent())
    }

    fun testJavaLang() {
        configure(
            """
                package io.nimbly;
                public class Person {
                    private Boolean zeBoolean;
                    private Character zeCharacter;
                    private String zeString;
                    private Number zeNumber;
                    private Double zeDouble;
                    private Float zeFloat;
                }"""
        )

        assertEquals(copy(), """
            {
              "zeBoolean": true,
              "zeCharacter": "w",
              "zeString": "Something",
              "zeNumber": 100,
              "zeDouble": 9999900000.0,
              "zeFloat": 9999900000.000000
            }
        """.trimIndent())
    }

    fun testJavaMath() {
        configure(
            """
                package io.nimbly;
                import java.math.BigDecimal;
                public class Person {
                    private BigDecimal zeBigDecimal;
                }"""
        )

        assertEquals(copy(), """
            {
              "zeBigDecimal": 9999900000.000000000000
            }
        """.trimIndent())
    }

    fun testJavaTime() {
        configure(
            """
                package io.nimbly;
                import java.util.Date;
                import java.time.LocalDateTime;
                import java.time.LocalDate;
                import java.time.LocalTime;
                public class Person {
                    public Date zeDate;
                    public LocalDateTime zeLocalDateTime;
                    public LocalDate zeLocalDate;
                    public LocalTime zeLocalTime;
                }"""
        )

        assertEquals(copy(), """
            {
              "zeDate": "2020-03-23 00:00:00",
              "zeLocalDateTime": "2020-03-23 00:00:00",
              "zeLocalDate": "2020-03-23",
              "zeLocalTime": "00:00:00"
            }
        """.trimIndent())
    }
}