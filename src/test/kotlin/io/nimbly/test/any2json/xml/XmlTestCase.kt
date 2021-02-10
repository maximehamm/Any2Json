package io.nimbly.test.any2json.xml

import io.nimbly.test.any2json.AbstractTestCase

class XmlTestCase : AbstractTestCase() {

    fun testXml() {

        // language=Xml
        configure("""
            <book id="bk101">
                <author>Gambardella, Matthew</author>
                <title>XML Developer's Guide</title>
                <genre>Computer</genre>
                <price>44.95</price>
                <publish_date>2000-10-01</publish_date>
                <description>An in-depth look at creating applications
                    with XML.</description>
            </book>
            """)

        // language=Json
        assertEquals(toJson(), """
            {
              "book": {
                "author": "Gambardella, Matthew",
                "price": 44.95,
                "genre": "Computer",
                "description": "An in-depth look at creating applications\n        with XML.",
                "id": "bk101",
                "title": "XML Developer\u0027s Guide",
                "publish_date": "2000-10-01"
              }
            }
        """.trimIndent())
    }



    fun configure(text: String) {
        val trimed = text.trimIndent()
        myFixture.configureByText("test.xml", StringBuilder(trimed).insert(
            trimed.indexOf('>')+1, "<caret>").toString()
        )
    }
}