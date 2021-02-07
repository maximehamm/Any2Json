package io.nimbly.test.any2json.java

import com.intellij.testFramework.PsiTestUtil
import io.nimbly.test.any2json.AbstractTestCase
import org.junit.Ignore

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

        val regex = """(class )|(interface )(\w*)""".toRegex()
        val className = regex.find(text.trimEnd())!!.groupValues.last()

        myFixture.configureByText("$className.java", t)
    }

    protected fun addClass(text: String) {
        myFixture.addClass(text)
    }

    override fun setUp() {
        super.setUp()

        PsiTestUtil.addLibrary( myFixture.module, getTestDataPath() + '/' + LIB_JAVA)

        addClass("""package java.math; public class BigDecimal extends Number { }""".trimIndent())
        addClass("""package java.time; public class LocalDate { }""".trimIndent())
        addClass("""package java.time; public class LocalDateTime { }""".trimIndent())
        addClass("""package java.time; public class LocalTime { }""".trimIndent())
    }
}