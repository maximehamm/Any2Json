package io.nimbly.test.any2json.java

import com.intellij.testFramework.PsiTestUtil
import io.nimbly.test.any2json.AbstractTestCase
import org.junit.Ignore

@Ignore
abstract class AbstractJavaTestCase : AbstractTestCase() {

    val LIB_JAVA = "/lib/rt-small.jar"

    fun configure(text: String)
        = super.configure(text, "java")

    override fun setUp() {
        super.setUp()

        PsiTestUtil.addLibrary( myFixture.module, getTestDataPath() + '/' + LIB_JAVA)

        addClass("""package java.math; public class BigDecimal extends Number { }""".trimIndent())
        addClass("""package java.time; public class LocalDate { }""".trimIndent())
        addClass("""package java.time; public class LocalDateTime { }""".trimIndent())
        addClass("""package java.time; public class LocalTime { }""".trimIndent())
    }
}