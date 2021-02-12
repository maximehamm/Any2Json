package io.nimbly.any2json.kotlin.test.javakot

import io.nimbly.any2json.kotlin.test.AbstractTestCase
import org.junit.Ignore

@Ignore
abstract class AbstractJavaKotlinTestCase : AbstractTestCase() {

    override fun setUp() {
        super.setUp()

        setupForJava()
        setupForKotlin()
    }
}