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

package io.nimbly.any2json.test.xml

import io.nimbly.any2json.test.AbstractTestCase

class XmlTestCase : AbstractTestCase() {

    fun testXmlNoSelection() {

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
        assertEquals(copy(), """
            {
              "book": {
                "author": "Gambardella, Matthew",
                "price": 44.95,
                "genre": "Computer",
                "description": "An in-depth look at creating applications\n        with XML.",
                "id": "bk101",
                "title": "XML Developer's Guide",
                "publish_date": "2000-10-01"
              }
            }
        """.trimIndent())
    }

    fun testXmlSelection() {

        // language=Xml
        configure("""
            <planes_for_sale>
               <ad>
                  <year> 1977 </year>
                  <model> Skyhawk </model>
                  <color> Light blue and white </color>
                  <description> New paint, nearly new interior, 685 hours SMOH, full IFR King avionics </description>
                  <price> 23,495 </price>
                  <seller phone = "555-222-3333"> Skyway Aircraft </seller>
                  <location>`caret`
                     <city> Rapid City, </city>
                     <state> South Dakota </state>
                  </location>
               </ad>
            </planes_for_sale>
            """)

        // language=Json
        assertEquals(copy(), """
            {
              "location": {
                "city": "Rapid City,",
                "state": "South Dakota"
              }
            }
        """.trimIndent())
    }


    fun configure(text: String) {
        val trimed = text.trimIndent().replace("`caret`", "<caret>")
        myFixture.configureByText("test.xml", StringBuilder(trimed).insert(
            trimed.indexOf('>')+1, "<caret>").toString()
        )
    }
}