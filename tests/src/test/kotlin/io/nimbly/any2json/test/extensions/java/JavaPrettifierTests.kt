package io.nimbly.any2json.test.extensions.java

import io.nimbly.any2json.test.extensions.AbstractJavaTestCase

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
    }
}