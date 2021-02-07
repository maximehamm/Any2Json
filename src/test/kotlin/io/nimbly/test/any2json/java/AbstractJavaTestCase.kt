package io.nimbly.test.any2json.java

import com.intellij.testFramework.PsiTestUtil
import io.nimbly.any2json.generator.TEST_BOOL
import io.nimbly.any2json.generator.TEST_CHAR
import io.nimbly.any2json.generator.TEST_DOUBLE
import io.nimbly.any2json.generator.TEST_INT
import io.nimbly.any2json.generator.TEST_NOW
import io.nimbly.test.any2json.AbstractTestCase
import org.junit.Ignore
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Month

@Ignore
abstract class AbstractJavaTestCase : AbstractTestCase() {

    protected fun configure(text: String) {

        var t = text.trimIndent()
        if (!t.contains("import java.lang.Boolean;")) {
            t = t.substringBefore("\n") + "\n" + """
            import java.lang.Boolean;
            import java.lang.String;
            import java.lang.Character;
            import java.lang.CharSequence;
            import java.lang.Number;
            import java.lang.Double;
            import java.lang.Long;
            import java.lang.Integer;
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

        configureBase(t)
    }

    protected fun configureBase(text: String) {
        val regex = """(class )|(interface )(\w*)""".toRegex()
        val className = regex.find(text)!!.groupValues.last()

        myFixture.configureByText("$className.java", text)
    }

    override fun setUp() {
        super.setUp()

        PsiTestUtil.addLibrary( myFixture.module, getTestDataPath() + '/' + LIB_JAVA)

        //configure("""package java.lang; public class Object""".trimIndent())
        configureBase("""package java.math; public class BigDecimal extends Number { }""".trimIndent())
        configureBase("""package java.time; public class LocalDate { }""".trimIndent())
        configureBase("""package java.time; public class LocalDateTime { }""".trimIndent())
        configureBase("""package java.time; public class LocalTime { }""".trimIndent())
    }
}