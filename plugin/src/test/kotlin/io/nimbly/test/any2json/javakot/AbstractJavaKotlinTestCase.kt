package io.nimbly.test.any2json.javakot

import io.nimbly.test.any2json.AbstractTestCase
import org.junit.Ignore

@Ignore
abstract class AbstractJavaKotlinTestCase : AbstractTestCase() {

    override fun setUp() {
        super.setUp()

        setupForJava()
        setupForKotlin()
    }
}