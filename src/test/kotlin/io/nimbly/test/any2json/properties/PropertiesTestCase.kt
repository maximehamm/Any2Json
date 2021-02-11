package io.nimbly.test.any2json.properties

import io.nimbly.test.any2json.AbstractTestCase
import org.junit.Ignore

class PropertiesTestCase : AbstractTestCase() {

    fun testProperties() {

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
        assertEquals(toJson(), """
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



    fun configure(text: String) {
        myFixture.configureByText("test.csv", text.trimIndent())
    }
}