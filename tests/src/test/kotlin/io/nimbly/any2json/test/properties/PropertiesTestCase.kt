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

package io.nimbly.any2json.test.properties

import io.nimbly.any2json.test.AbstractTestCase

class PropertiesTestCase : AbstractTestCase() {

    fun testPropertiesNoSelection() {

        // language=Properties
        configure("""
            spring.rabbitmq.dynamic=true
            spring.rabbitmq.port=5672
            spring.rabbitmq.username=guest
            spring.rabbitmq.password=guest
            spring.rabbitmq.host=localhost
            
            git.user.default.login=default#User
            git.user.default.email=defaultUser@akwatype.io
            git.user.default=Maxime
            
            git.user.default.gitUser=
            git.user.default.gitPassword=1234a*=12
            git.user.default.gitFolder=/tmp/git
            """)

        // language=Json
//        assertEquals(toJson(), """
//            {
//              "spring.rabbitmq.dynamic": "true",
//              "spring.rabbitmq.password": "guest",
//              "spring.rabbitmq.port": "5672",
//              "git.user.default.gitFolder": "/tmp/git",
//              "spring.rabbitmq.host": "localhost",
//              "git.user.default": "Maxime",
//              "git.user.default.gitPassword": "1234a*=12",
//              "git.user.default.login": "default#User",
//              "git.user.default.gitUser": "",
//              "spring.rabbitmq.username": "guest",
//              "git.user.default.email": "defaultUser@akwatype.io"
//            }
//        """.trimIndent())

        // language=Json
        assertEquals(copy(), """
            {
              "git": {
                "user": {
                  "default": [
                    "Maxime",
                    {
                      "email": "defaultUser@akwatype.io",
                      "gitFolder": "/tmp/git",
                      "gitPassword": "1234a*=12",
                      "gitUser": "",
                      "login": "default#User"
                    }
                  ]
                }
              },
              "spring": {
                "rabbitmq": {
                  "dynamic": "true",
                  "host": "localhost",
                  "password": "guest",
                  "port": "5672",
                  "username": "guest"
                }
              }
            }
        """.trimIndent())
    }

    fun testPropertiesWithSelection() {

        // language=Properties
        configure("""
            spring.rabbitmq.dynamic=true
            spring.rabbitmq.port=5672
            spring.rabbitmq.username=guest
            spring.rabbitmq.password=guest
            spring.rabbitmq.host=localhost
            `from`git.user.default.login=default#User
            git.user.default.email=defaultUser@akwatype.io
            git.user.default=Maxime`to`
            git.user.default.gitUser=
            git.user.default.gitPassword=1234a*=12
            git.user.default.gitFolder=/tmp/git
            """)

        // language=Json
//        assertEquals(toJson(), """
//            {
//              "git.user.default": "Maxime",
//              "git.user.default.login": "default#User",
//              "git.user.default.email": "defaultUser@akwatype.io"
//            }
//        """.trimIndent())

        // language=Json
        assertEquals(copy(), """
            {
              "git": {
                "user": {
                  "default": [
                    "Maxime",
                    {
                      "email": "defaultUser@akwatype.io",
                      "login": "default#User"
                    }
                  ]
                }
              }
            }
        """.trimIndent())
    }



    fun configure(text: String)
        = configure(text, "properties")
}