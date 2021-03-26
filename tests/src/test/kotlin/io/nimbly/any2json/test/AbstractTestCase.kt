/*
 * ANY2JSON
 * Copyright (C) 2021  Maxime HAMM - NIMBLY CONSULTING - maxime.hamm.pro@gmail.com
 *
 * This document is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This work is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package io.nimbly.any2json.test

import com.intellij.openapi.actionSystem.ex.ActionManagerEx
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.testFramework.PsiTestUtil
import com.intellij.testFramework.fixtures.JavaCodeInsightFixtureTestCase
import com.intellij.testFramework.fixtures.kotlin.KotlinTester
import io.nimbly.any2json.*
import io.nimbly.any2json.test.AbstractTestCase.EXT.java
import io.nimbly.any2json.test.AbstractTestCase.EXT.kt
import io.nimbly.any2json.util.resetLastNotification
import junit.framework.TestCase
import org.junit.Ignore
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Month

@Ignore
abstract class AbstractTestCase : JavaCodeInsightFixtureTestCase() {

    enum class EXT { java, kt }

    val LIB_JAVA = "/lib/rt-small.jar"
    val LIB_KOTLIN = "/lib/kotlin-stdlib-1.4.30.jar"

    var configuredFile: PsiFile? = null

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
        configuredFile = myFixture.configureByText("$className.${extension.name}", t)
    }

    protected open fun findFileInTempDir(filePath: String): VirtualFile? {
        val fullPath = myFixture.tempDirPath + "/" + filePath
        return LocalFileSystem.getInstance().refreshAndFindFileByPath(fullPath.replace(File.separatorChar, '/'))
    }

    protected fun toJson()
        = innerToJson("io.nimbly.any2json.ANY2JsonDefaultAction")

    protected fun toJson2()
        = innerToJson("io.nimbly.any2json.ANY2JsonRandomAction")

    protected fun copy()
        = innerToJson("io.nimbly.any2json.Any2JsonCopyAction")

    private fun innerToJson(actionId: String): String {

        resetLastNotification()

        val action = ActionManagerEx.getInstanceEx().getAction(actionId)
        val presentation = myFixture.testAction(action)

        if (!presentation.isEnabled)
            return "Not enabled"

        if (!presentation.isVisible)
            return "Not visible"

        return Toolkit.getDefaultToolkit().systemClipboard.getData(DataFlavor.stringFlavor).toString()
    }

    protected fun prettify(): String {

        resetLastNotification()

        val action = ActionManagerEx.getInstanceEx().getAction("io.nimbly.any2json.Any2JsonPrettifyAction")
        val presentation = myFixture.testAction(action)

        if (!presentation.isEnabled)
            return "Not enabled"

        if (!presentation.isVisible)
            return "Not visible"

        return myFixture.editor.document.text
            .replace(Regex("""import ([a-z]|[A-Z]|[.])*;\n"""), "")
    }

    fun configure(text: String, extension: String) {

        val trimmed = text.trimIndent()

        if (trimmed.contains("`from`")) {
            val i = trimmed.indexOf("`from`")
            val j = trimmed.indexOf("`to`")

            val t = trimmed.substring(0, i) + trimmed.substring(i+6, j) + trimmed.substring(j+4)
            myFixture.configureByText("test.$extension", t)
            myFixture.editor.selectionModel.setSelection(i, j-6)

            val selection = myFixture.editor.document.getText(
                TextRange(myFixture.editor.selectionModel.selectionStart, myFixture.editor.selectionModel.selectionEnd)
            )
        }
        else {
            myFixture.configureByText("test.$extension", trimmed.trimIndent())
        }
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