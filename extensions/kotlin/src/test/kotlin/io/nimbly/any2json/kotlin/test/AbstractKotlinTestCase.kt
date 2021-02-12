package io.nimbly.any2json.kotlin.test

import org.junit.Ignore

@Ignore
abstract class AbstractKotlinTestCase : AbstractTestCase() {

    fun configure(text: String)
        = super.configure(EXT.kt, text)

    override fun setUp() {
        super.setUp()
        setupForKotlin()
    }

    fun addClass(text: String)
            = addClass(EXT.kt, text)
}