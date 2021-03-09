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