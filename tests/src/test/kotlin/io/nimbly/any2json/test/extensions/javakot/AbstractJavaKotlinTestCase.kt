package io.nimbly.any2json.test.extensions.javakot

import io.nimbly.any2json.test.AbstractTestCase
import org.junit.Ignore

@Ignore
abstract class AbstractJavaKotlinTestCase : AbstractTestCase() {

    override fun setUp() {
        super.setUp()

        setupForJava()
        setupForKotlin()
    }
}