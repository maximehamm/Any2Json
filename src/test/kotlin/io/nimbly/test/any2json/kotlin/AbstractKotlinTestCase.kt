package io.nimbly.test.any2json.kotlin

import com.intellij.openapi.roots.ModuleRootModificationUtil
import com.intellij.testFramework.PsiTestUtil
import com.intellij.testFramework.fixtures.kotlin.KotlinTester
import io.nimbly.test.any2json.AbstractTestCase
import org.junit.Ignore

@Ignore
abstract class AbstractKotlinTestCase : AbstractTestCase() {

    val LIB_KOTLIN = "/lib/kotlin-stdlib-1.4.30.jar"

    fun configure(text: String)
        = super.configure(text, "kt")

    override fun setUp() {
        super.setUp()

        PsiTestUtil.addLibrary( myFixture.module, getTestDataPath() + '/' + LIB_KOTLIN)
        KotlinTester.assumeCanUseKotlin()

        addClass("""package kotlin; public class BigDecimal : Number { }""".trimIndent())
        addClass("""package java.time; public class LocalDate { }""".trimIndent())
        addClass("""package java.time; public class LocalDateTime { }""".trimIndent())
        addClass("""package java.time; public class LocalTime { }""".trimIndent())

        addClass("""package java.util; public class Date { }""".trimIndent())
    }
}