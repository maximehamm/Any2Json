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