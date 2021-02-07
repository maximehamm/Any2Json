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
            import java.lang.Float;
            import java.lang.Character;
            """.trimIndent() + t.substringAfter(";")
        }

        val regex = """( class )(\w*)""".toRegex()
        val className = regex.find(text)!!.groupValues.last()

        myFixture.configureByText("$className.java", t)
    }

    override fun setUp() {
        super.setUp()

        PsiTestUtil.addLibrary( myFixture.module, getTestDataPath() + '/' + LIB_JAVA)

        //configure("""package java.lang; public class Object""".trimIndent())
        configure("""package java.math; public class BigDecimal extends Number { }""".trimIndent())
        configure("""package java.time; public class LocalDate { }""".trimIndent())
        configure("""package java.time; public class LocalDateTime { }""".trimIndent())
        configure("""package java.time; public class LocalTime { }""".trimIndent())
    }
}