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

package io.nimbly.any2json.test.json

import io.nimbly.any2json.test.AbstractTestCase

class JsonTestCase : AbstractTestCase() {

    fun testJson() {

        // language=Json
        configure("""
            [ { "hosts": "webservers",
                "vars": {
                  "http_port": 80,
                     "max_clients": 200
                },
                "remote_user": "root",
                "tasks": [
                  {
                "name": "ensure apache is at the latest version",
                "yum": {
                  "name": "httpd",
                  "state": "latest"
                }
                  }
                ]
              }
            ]
        """)

        // language=Json
        assertEquals(copy(), """
        [
          {
            "hosts": "webservers",
            "vars": {
              "http_port": 80,
              "max_clients": 200
            },
            "remote_user": "root",
            "tasks": [
              {
                "name": "ensure apache is at the latest version",
                "yum": {
                  "name": "httpd",
                  "state": "latest"
                }
              }
            ]
          }
        ]
        """.trimIndent())

        // language=Json
        assertEquals(prettify(), """
        [
          {
            "hosts": "webservers",
            "vars": {
              "http_port": 80,
              "max_clients": 200
            },
            "remote_user": "root",
            "tasks": [
              {
                "name": "ensure apache is at the latest version",
                "yum": {
                  "name": "httpd",
                  "state": "latest"
                }
              }
            ]
          }
        ]
        """.trimIndent())
    }



    fun configure(text: String) {
        myFixture.configureByText("test.json", text.trimIndent())
    }
}