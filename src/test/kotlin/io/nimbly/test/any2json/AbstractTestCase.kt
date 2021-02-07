package io.nimbly.test.any2json

import com.intellij.testFramework.fixtures.JavaCodeInsightFixtureTestCase
import io.nimbly.any2json.generator.TEST_BOOL
import io.nimbly.any2json.generator.TEST_CHAR
import io.nimbly.any2json.generator.TEST_DOUBLE
import io.nimbly.any2json.generator.TEST_INT
import io.nimbly.any2json.generator.TEST_NOW
import junit.framework.TestCase
import org.junit.Ignore
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Month

@Ignore
abstract class AbstractTestCase : JavaCodeInsightFixtureTestCase() {

    protected open fun configure(text: String, extension: String) {

        var t = text.trimIndent().trim()

        TestCase.assertTrue(t.startsWith("package"))

        if (!t.contains("import java.lang.Boolean")) {
            t = t.substringBefore("\n") + "\n" + """
            import java.lang.Boolean;
            import java.lang.String;
            import java.lang.Character;
            import java.lang.CharSequence;
            import java.lang.Number;
            import java.lang.Double;
            import java.lang.Long;
            import java.lang.Integer;
            import java.lang.Number;
            import java.lang.Float;
            import java.lang.Character;
            """.trimIndent() + t.substringAfter(";")
        }

        if (!t.contains("<caret>")) {
            if (t.contains("class "))
                t = t.substringBefore("class ") + "class <caret>" +
                        t.substringAfter("class ")
            else
                t = t.substringBefore("interface ") + "interface <caret>" +
                        t.substringAfter("interface ")
        }

        val regex = """(class|interface) *([\w]+)""".toRegex()
        val className = regex.find(text.trimIndent())!!.groupValues.last()

        myFixture.configureByText("$className.$extension", t)
    }

    protected fun addClass(text: String) {
        myFixture.addClass(text)
    }

    protected fun toJson(): String {
        myFixture.performEditorAction("io.nimbly.any2json.ANY2JsonDefaultAction")
        return Toolkit.getDefaultToolkit().systemClipboard.getData(DataFlavor.stringFlavor).toString()
    }

    protected fun toJsonRandom(): String {
        myFixture.performEditorAction("io.nimbly.any2json.ANY2JsonRandomAction")
        return Toolkit.getDefaultToolkit().systemClipboard.getData(DataFlavor.stringFlavor).toString()
    }

    override fun setUp() {
        super.setUp()

        TEST_NOW = LocalDateTime.of(LocalDate.of(2020, Month.MARCH,23), LocalTime.MIDNIGHT)
        TEST_DOUBLE = 9999.9
        TEST_INT = 99
        TEST_CHAR = 'w'
        TEST_BOOL = true
    }

    override fun getTestDataPath(): String? {
        return "src/test/resources"
    }
}