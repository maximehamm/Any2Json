package io.nimbly.test.any2json.java

class JavaArraysTests : AbstractJavaTestCase() {

    fun testArraysBase() {
        configure("""
                package io.nimbly;
                import java.util.List;
                import java.util.Collection;
                public class Person<caret> {
                    private List<Boolean> zeBooleans;
                    private Collection<Character> zeCharacters;
                    private java.util.ArrayList<String> zeStrings;
                    private Iterator<Number> zeNumbers;
                }""")

        assertEquals(toJson(), """
            {
              "zeBooleans": [
                false
              ],
              "zeCharacters": [
                "a"
              ],
              "zeStrings": [
                ""
              ],
              "zeNumbers": {}
            }
        """.trimIndent())

        assertEquals(toJsonRandom(), """
            {
              "zeBooleans": [
                true
              ],
              "zeCharacters": [
                "w"
              ],
              "zeStrings": [
                "Something"
              ],
              "zeNumbers": {}
            }
        """.trimIndent())
    }

    fun testArraysNoType() {
        configure("""
                package io.nimbly;
                public class Person<caret> {
                    private java.util.List zeThings;
                }""")

        assertEquals(toJson(), """
            {
              "zeThings": []
            }
        """.trimIndent())
    }

}