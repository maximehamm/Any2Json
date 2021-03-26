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

package io.nimbly.any2json.test.javakot

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