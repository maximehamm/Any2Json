package io.nimbly.test.any2json

import com.intellij.testFramework.PsiTestUtil
import com.intellij.testFramework.fixtures.JavaCodeInsightFixtureTestCase
import com.intellij.testFramework.fixtures.kotlin.KotlinTester
import io.nimbly.any2json.generator.TEST_BOOL
import io.nimbly.any2json.generator.TEST_CHAR
import io.nimbly.any2json.generator.TEST_DOUBLE
import io.nimbly.any2json.generator.TEST_INT
import io.nimbly.any2json.generator.TEST_NOW
import io.nimbly.test.any2json.AbstractTestCase.EXT.java
import io.nimbly.test.any2json.AbstractTestCase.EXT.kt
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

    enum class EXT { java, kt, xml }

    val LIB_JAVA = "/lib/rt-small.jar"
    val LIB_KOTLIN = "/lib/kotlin-stdlib-1.4.30.jar"

    protected fun setupForJava() {
        PsiTestUtil.addLibrary(myFixture.module, getTestDataPath() + '/' + LIB_JAVA)

        addClass(java, """package java.math; public class BigDecimal extends Number { }""".trimIndent())
        addClass(java, """package java.time; public class LocalDate { }""".trimIndent())
        addClass(java, """package java.time; public class LocalDateTime { }""".trimIndent())
        addClass(java, """package java.time; public class LocalTime { }""".trimIndent())
    }

    protected fun setupForKotlin() {
        PsiTestUtil.addLibrary(myFixture.module, getTestDataPath() + '/' + LIB_KOTLIN)
        KotlinTester.assumeCanUseKotlin()

        addClass(kt, """package kotlin; public class BigDecimal : Number { }""".trimIndent())
        addClass(kt, """package java.time; public class LocalDate { }""".trimIndent())
        addClass(kt, """package java.time; public class LocalDateTime { }""".trimIndent())
        addClass(kt, """package java.time; public class LocalTime { }""".trimIndent())

        addClass(kt, """package java.util; public class Date { }""".trimIndent())
    }

    fun addClass(extension: EXT, text: String) {

        if (extension == java) {

            // Java
            myFixture.addClass(text)
        }
        else if (extension == kt) {

            // Kotlin
            val regex = """(class|interface) *([\w]+)""".toRegex()
            val className = regex.find(text.trimIndent())!!.groupValues.last()
            myFixture.configureByText("$className.kt", text)
        }
    }

    protected open fun configure(extension: EXT, text: String) {

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

        myFixture.configureByText("$className.${extension.name}", t)
    }

    protected fun toJson(): String {
        myFixture.performEditorAction("io.nimbly.any2json.ANY2JsonDefaultAction")
        return Toolkit.getDefaultToolkit().systemClipboard.getData(DataFlavor.stringFlavor).toString()
    }

    protected fun toJson2(): String {
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