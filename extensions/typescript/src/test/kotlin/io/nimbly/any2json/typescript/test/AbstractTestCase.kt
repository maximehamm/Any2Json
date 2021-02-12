package io.nimbly.any2json.typescript.test

import com.intellij.testFramework.fixtures.JavaCodeInsightFixtureTestCase
import io.nimbly.any2json.TEST_BOOL
import io.nimbly.any2json.TEST_CHAR
import io.nimbly.any2json.TEST_DOUBLE
import io.nimbly.any2json.TEST_INT
import io.nimbly.any2json.TEST_NOW
import org.junit.Ignore
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Month

@Ignore
abstract class AbstractTestCase : JavaCodeInsightFixtureTestCase() {

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