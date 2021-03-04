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
        assertEquals(toJson(), """
         xxxx
        """.trimIndent())
    }



    fun configure(text: String) {
        myFixture.configureByText("test.json", text.trimIndent())
    }
}