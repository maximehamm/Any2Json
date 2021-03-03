package io.nimbly.any2json.test.extensions.kotlin

import io.nimbly.any2json.lastNotification
import io.nimbly.any2json.test.extensions.AbstractKotlinTestCase

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
        assertEquals("Not a valid Json or Xml !", lastNotification())

        copy()
        assertEquals("Not a valid Json or Xml !", lastNotification())
    }
}