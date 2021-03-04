package io.nimbly.any2json.test.kotlin

import io.nimbly.any2json.NO_CONVERSION_FOUND
import io.nimbly.any2json.lastNotification
import io.nimbly.any2json.test.AbstractKotlinTestCase

class KotlinPrettifierConversionTests : AbstractKotlinTestCase() {

    fun testFromXml() {

        // language=Kt
        configure("""
                package io.nimbly;
                class Test {
                    fun test() {
                       val xml = ""${'"'}<catalog><caret>
                        <book id="bk101">
                            <author>Gambardella, Matthew</author>
                            <title>XML Developer's Guide</title>
                            <genre>Computer</genre>
                            <price>44.95</price>
                            <publish_date>2000-10-01</publish_date>
                            <description>An in-depth look at creating applications
                                with XML.</description>
                        </book>
                    </catalog>""${'"'}.trimIndent()
                    }
                }""")

        // language=Kt
        assertEquals(prettify(), """
                package io.nimbly;
                class Test {
                    fun test() {
                       val xml = ""${'"'}{
                             "catalog": {
                               "book": {
                                 "author": "Gambardella, Matthew",
                                 "price": 44.95,
                                 "genre": "Computer",
                                 "description": "An in-depth look at creating applications\\n                with XML.",
                                 "id": "bk101",
                                 "title": "XML Developer's Guide",
                                 "publish_date": "2000-10-01"
                               }
                             }
                           }""${'"'}.trimIndent()
                    }
                }""".trimIndent())

        // language=Json
        assertEquals(copy(), """
                {
                  "catalog": {
                    "book": {
                      "author": "Gambardella, Matthew",
                      "price": 44.95,
                      "genre": "Computer",
                      "description": "An in-depth look at creating applications\\n                with XML.",
                      "id": "bk101",
                      "title": "XML Developer's Guide",
                      "publish_date": "2000-10-01"
                    }
                  }
                }""".trimIndent())
    }

    fun testFromCsv() {

        // language=Kt
        configure("""
                package io.nimbly;
                class Test {
                    fun test() {
                       val csv = ""${'"'}<caret>
                           Identifier;Access code;Recovery code;First name;Last name;Department;Location;Description
                           5079;09ja61;js5079;Jamie;Smith;Engineering;;"Quit good; let's see !"
                           4081;;;;;;;The description !
                           ""${'"'}.trimIndent()
                    }
                }""")

        // language=Kt
        assertEquals(prettify(), """
                package io.nimbly;
                class Test {
                    fun test() {
                       val csv = ""${'"'}[
                             {
                               "Identifier": "5079",
                               "Access code": "09ja61",
                               "Recovery code": "js5079",
                               "First name": "Jamie",
                               "Last name": "Smith",
                               "Department": "Engineering",
                               "Location": null,
                               "Description": "Quit good; let's see !"
                             },
                             {
                               "Identifier": "4081",
                               "Access code": null,
                               "Recovery code": null,
                               "First name": null,
                               "Last name": null,
                               "Department": null,
                               "Location": null,
                               "Description": "The description !"
                             }
                           ]""${'"'}.trimIndent()
                    }
                }""".trimIndent())

        // language=Json
        assertEquals(copy(), """
                [
                  {
                    "Identifier": "5079",
                    "Access code": "09ja61",
                    "Recovery code": "js5079",
                    "First name": "Jamie",
                    "Last name": "Smith",
                    "Department": "Engineering",
                    "Location": null,
                    "Description": "Quit good; let's see !"
                  },
                  {
                    "Identifier": "4081",
                    "Access code": null,
                    "Recovery code": null,
                    "First name": null,
                    "Last name": null,
                    "Department": null,
                    "Location": null,
                    "Description": "The description !"
                  }
                ]""".trimIndent())
    }

    fun testFromYaml() {

        // language=Yaml
        configure("""
                package io.nimbly;
                class Test {
                    fun test() {
                       val yaml = ""${'"'}<caret>
                            ---
                            - hosts: webservers
                            
                              vars:
                                http_port: 80
                                max_clients: 200
                            
                              remote_user: root
                            
                              tasks:
                                - name: ensure apache is at the latest version
                                  yum:
                                    name: httpd
                                    state: latest
                            
                              handlers:
                                - name: restart apache
                                  service:
                                    name: httpd
                                    state: restarted
                            ""${'"'}.trimIndent()
                    }
                }""")

        // language=Yaml
        assertEquals(prettify(), """
                package io.nimbly;
                class Test {
                    fun test() {
                       val yaml = ""${'"'}[
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
                               ],
                               "handlers": [
                                 {
                                   "name": "restart apache",
                                   "service": {
                                     "name": "httpd",
                                     "state": "restarted"
                                   }
                                 }
                               ]
                             }
                           ]""${'"'}.trimIndent()
                    }
                }""".trimIndent())

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
                ],
                "handlers": [
                  {
                    "name": "restart apache",
                    "service": {
                      "name": "httpd",
                      "state": "restarted"
                    }
                  }
                ]
              }
            ]""".trimIndent())
    }

    fun testFromProperties() {

        // language=Kt
        configure("""
                package io.nimbly;
                class Test {
                    fun test() {
                       val properties = ""${'"'}<caret>
                            spring.rabbitmq.dynamic=true
                            spring.rabbitmq.port=5672
                            spring.rabbitmq.username=guest
                            spring.rabbitmq.password=guest
                            spring.rabbitmq.host=localhost
                            git.user.default.login=default#User
                            git.user.default.email=defaultUser@akwatype.io
                            git.user.default=Maxime""${'"'}.trimIndent()
                    }
                }""")

        // language=Kt
        assertEquals(prettify(), """
            package io.nimbly;
            class Test {
                fun test() {
                   val properties = ""${'"'}{
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
                       }""${'"'}.trimIndent()
                }
            }""".trimIndent())

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
            }""".trimIndent())
    }

    fun testFromUnknown() {

        // language=Kt
        configure("""
                package io.nimbly;
                class Test {
                    fun test() {
                       val xml = "<caret>nothing at all"
                    }
                }""")

        prettify()
        assertEquals(NO_CONVERSION_FOUND, lastNotification())

        copy()
        assertEquals(NO_CONVERSION_FOUND, lastNotification())
    }

    fun testBrokenXml() {

        // language=Kt
        configure("""
                package io.nimbly;
                class Test {
                    fun test() {
                       val xml = ""${'"'}<catalog><caret>
                        <book id="bk101">
                            <authxxxxxxxxxxxxxxxxxxxor>Gambardella, Matthew</author>
                            <title>XML Developer's Guide</title>
                            <genre>Computer</genre>
                            <price>44.95</price>
                            <publish_date>2000-10-01</publish_date>
                            <description>An in-depth look at creating applications
                                with XML.</description>
                        </book>
                    </catalog>""${'"'}.trimIndent()
                    }
                }""")

        prettify()
        assertEquals(NO_CONVERSION_FOUND, lastNotification())

        copy()
        assertEquals(NO_CONVERSION_FOUND, lastNotification())
    }

    fun testBrokenJson() {

        // language=Kt
        configure("""
                package io.nimbly;
                class Test {
                    fun test() {
                        val before = ""${'"'}{<caret>
                          "id": 6,
                          "type"
                          "revision": 100,
                          "history": [ ......
                            {
                              "lenght": 55,
                              "depth": 77
                            }
                          ]
                        }""${'"'}.toString()
                    }
                }""")

        prettify()
        assertEquals(NO_CONVERSION_FOUND, lastNotification())

        copy()
        assertEquals(NO_CONVERSION_FOUND, lastNotification())
    }

    fun testBrokenYaml() {

        // language=Yaml
        configure("""
                package io.nimbly;
                class Test {
                    fun test() {
                       val xml = ""${'"'}<caret>
                            ---
                                - hosts: webservers
                            
                              vars:
                                http_port: 80
                                max_clients: 200
                            
                              remote_user: root
                            
                              tasks:
                                - name: ensure apache is, at the latest version
                                  yum:
                                    name: httpd
                                    state: latest
                            
                              handlers:
                                - name: restart apache
                                  service:
                                    name: httpd
                                    state: restarted
                            ""${'"'}.trimIndent()
                    }
                }""")

        prettify()
        assertEquals(NO_CONVERSION_FOUND, lastNotification())

        copy()
        assertEquals(NO_CONVERSION_FOUND, lastNotification())
    }
}