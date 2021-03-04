package io.nimbly.any2json.test.csv

import io.nimbly.any2json.test.AbstractTestCase

class CsvTestCase : AbstractTestCase() {

    fun testCsvNoSelection() {

        // language=Csv
        configure("""
            Identifier;Access code;Recovery code;First name;Last name;Department;Location;Description
            9012;12se74;rb9012;Rachel;Booker';Sales;Manchester;The description
            5079;09ja61;js5079;Jamie;Smith;Engineering;;"Quit good; let's see !"
            4081;;;;;;;The description !
            2070;04ap67;lg2070;Laura;Grey;Depot;London;The description
            9346;14ju73;mj9346;Mary;Jenkins;Engineering;Manchester;The description
            """)

        // language=Json
        assertEquals(toJson(), """
            [
              {
                "Identifier": "9012",
                "Access code": "12se74",
                "Recovery code": "rb9012",
                "First name": "Rachel",
                "Last name": "Booker'",
                "Department": "Sales",
                "Location": "Manchester",
                "Description": "The description"
              },
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
              },
              {
                "Identifier": "2070",
                "Access code": "04ap67",
                "Recovery code": "lg2070",
                "First name": "Laura",
                "Last name": "Grey",
                "Department": "Depot",
                "Location": "London",
                "Description": "The description"
              },
              {
                "Identifier": "9346",
                "Access code": "14ju73",
                "Recovery code": "mj9346",
                "First name": "Mary",
                "Last name": "Jenkins",
                "Department": "Engineering",
                "Location": "Manchester",
                "Description": "The description"
              }
            ]
        """.trimIndent())
    }

    fun testCsvWithSelection() {

        // language=Csv
        configure("""
            Identifier;Access code;Recovery code;First name;Last name;Department;Location;Description
            9012;12se74;rb9012;Rachel;Booker';Sales;Manchester;The description
            `from`5079;09ja61;js5079;Jamie;Smith;Engineering;;"Quit good; let's see !"
            4081;;;;;;;The description !`to`
            2070;04ap67;lg2070;Laura;Grey;Depot;London;The description
            9346;14ju73;mj9346;Mary;Jenkins;Engineering;Manchester;The description
            """)

        // language=Json
        assertEquals(toJson(), """
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
            ]
        """.trimIndent())
    }


    fun configure(text: String)
        = configure(text, "csv")
}