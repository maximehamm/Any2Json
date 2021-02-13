package io.nimbly.any2json.test.extensions.java

import io.nimbly.any2json.test.extensions.AbstractJavaTestCase

class JavaBaseTests : AbstractJavaTestCase() {

    fun testPrimitives() {

        // language=Java
        configure("""
                package io.nimbly;
                public class Person {
                    private boolean zeBoolean;
                    private int zeInt;
                    private long zeLong;
                    private double zeDouble;
                    private float zeFloat;
                    private char zeChar;
                }""")

        // language=Json
        assertEquals(toJson(), """
            {
              "zeBoolean": false,
              "zeInt": 0,
              "zeLong": 0,
              "zeDouble": 0.0,
              "zeFloat": 0.000000,
              "zeChar": "a"
            }
        """.trimIndent())
    }

    fun testJavaLang() {

        // language=Java
        configure("""
                package io.nimbly;
                public class Person {
                    private Boolean zeBoolean;
                    private Character zeCharacter;
                    private String zeString;
                    private Number zeNumber;
                    private Double zeDouble;
                    private Float zeFloat;
                }""")

        // language=Json
        assertEquals(toJson(), """
            {
              "zeBoolean": false,
              "zeCharacter": "a",
              "zeString": "",
              "zeNumber": 0,
              "zeDouble": 0.0,
              "zeFloat": 0.000000
            }
        """.trimIndent())
    }

    fun testJavaMath() {

        // language=Java
        configure("""
                package io.nimbly;
                import java.math.BigDecimal;
                public class Person {
                    private BigDecimal zeBigDecimal;
                }""")

        // language=Json
        assertEquals(toJson(), """
            {
              "zeBigDecimal": 0E-12
            }
        """.trimIndent())
    }

    fun testJavaTime() {

        // language=Java
        configure("""
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
                }""")

        // language=Json
        assertEquals(toJson(), """
            {
              "zeDate": "2020-03-23 00:00:00",
              "zeLocalDateTime": "2020-03-23 00:00:00",
              "zeLocalDate": "2020-03-23",
              "zeLocalTime": "00:00:00"
            }
        """.trimIndent())
    }


    fun testIgnoreStatic() {

        // language=Java
        configure("""
            package io.nimbly;
            public class Person {
                public static final String TEST = "TEST";
                private int name;
            }""")

        // language=Json
        assertEquals(toJson(), """
            {
              "name": 0
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
        assertEquals(toJson2(), """
            {
              "id": 123456789
            }
        """.trimIndent())
    }
}