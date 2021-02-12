package io.nimbly.any2json.test.java

import io.nimbly.any2json.test.AbstractTestCase
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