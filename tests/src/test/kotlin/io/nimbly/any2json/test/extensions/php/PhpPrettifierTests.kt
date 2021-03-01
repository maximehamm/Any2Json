package io.nimbly.any2json.test.extensions.kotlin

import io.nimbly.any2json.test.AbstractTestCase

class PhpPrettifierTests : AbstractTestCase() {

    fun testSingleQuote() {

        // language=t
        configure("""
                <?php
                json = '<caret>{"a":1,"b":2,"c":3, "d":4, "e":5}';
                ?>
                """)

        // language=t
        assertEquals(prettify(), """
                <?php
                json = '{
                  "a": 1,
                  "b": 2,
                  "c": 3,
                  "d": 4,
                  "e": 5
                }';
                ?>
                """.trimIndent())
    }

    fun testDoubleQuote() {

        // language=t
        configure("""
                <?php
                json = "<caret>{\"a\":1,\"b\":2,\"c\":3, \"d\":4, \"e\":5}";
                ?>
                """)

        // language=t
        assertEquals(prettify(), """
                <?php
                json = "{
                  \"a\": 1,
                  \"b\": 2,
                  \"c\": 3,
                  \"d\": 4,
                  \"e\": 5
                }";
                ?>
                """.trimIndent())
    }

    fun testTag() {

        // language=t
        configure("""
                <?php
                json = <<<'TAG'<caret>
                {"a":1,"b":2,"c":3, "d":4, "e":5}
                TAG;
                ?>
                """)

        // language=t
        assertEquals(prettify(), """
                <?php
                json = <<<'TAG'
                {
                  "a": 1,
                  "b": 2,
                  "c": 3,
                  "d": 4,
                  "e": 5
                }
                TAG;
                ?>
                """.trimIndent())
    }

    private fun configure(text: String) {
        myFixture.configureByText("test.php", text.trimIndent())
    }
}