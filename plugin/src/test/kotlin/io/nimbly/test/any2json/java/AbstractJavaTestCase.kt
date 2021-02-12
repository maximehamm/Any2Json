package io.nimbly.test.any2json.java

import io.nimbly.test.any2json.AbstractTestCase
import org.junit.Ignore

@Ignore
abstract class AbstractJavaTestCase : AbstractTestCase() {

    fun configure(text: String)
        = super.configure(EXT.java, text)

    override fun setUp() {
        super.setUp()
        setupForJava()
    }

    fun addClass(text: String)
        = addClass(EXT.java, text)
}