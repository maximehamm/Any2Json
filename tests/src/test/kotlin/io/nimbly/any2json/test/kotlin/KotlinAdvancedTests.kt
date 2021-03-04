package io.nimbly.any2json.test.kotlin

import io.nimbly.any2json.test.AbstractKotlinTestCase

class KotlinAdvancedTests : AbstractKotlinTestCase() {

    fun testEnum() {

        // language=Kt
        configure("""
            package io.nimbly
            class Person {
                private val name: String? = null
                private val age: Int? = null
                private val gender: EGender? = null
            }    
            internal enum class EGender { MALE, FEMALE, OTHER }
            """)

        // language=Json
        assertEquals(toJson2(), """
            {
              "name": "Something",
              "age": 100,
              "gender": "OTHER"
            }
        """.trimIndent())
    }

    fun testArraysSimple() {

        // language=Kt
        configure("""
            package io.nimbly
            class School {
                private val schoolName: String? = null
                private val students: Set<Student>? = null
            }
            class Student : Person(), ISchool {
                private val school: School? = null
            }
            class Person {
                private val name: String? = null
                private val age: Int? = null
            }
            interface ISchool {
                companion object {
                    const val id = 123456789L
                }
            }           
            """)

        // language=Json
        assertEquals(toJson2(), """
            {
              "schoolName": "Something",
              "students": [
                {
                  "school": {
                    "schoolName": "Something",
                    "students": []
                  },
                  "age": 100,
                  "name": "Something",
                  "Companion": null
                }
              ]
            }
        """.trimIndent())
    }

    fun testArraysBold() {

        // language=Kt
        configure(
            """
            package io.nimbly
            
            class School {
                private val schoolName: String? = null
                private val students: Set<Student>? = null
                private val teatchers: Set<Teatcher>? = null
            }
            
            class Student : Person(), ISchool {
                private val school: School? = null
                private val teatchers: Set<Teatcher>? = null
            }
            
            internal class Teatcher : Person(), ISchool {
                private val shcool: School? = null
                private val students: Set<Student>? = null
            }
            
            open class Person {
                private val name: String? = null
                private val age: Int? = null
            }
            
            internal interface ISchool {
                companion object {
                    const val id = 123456789L
                }
            }   
            """
        )

        // language=Json
        assertEquals(toJson2(), """
            {
              "schoolName": "Something",
              "students": [
                {
                  "school": {
                    "teatchers": [
                      {
                        "shcool": null,
                        "students": [],
                        "age": 100,
                        "name": "Something",
                        "Companion": null
                      }
                    ],
                    "schoolName": "Something",
                    "students": []
                  },
                  "teatchers": [],
                  "age": 100,
                  "name": "Something",
                  "Companion": null
                }
              ],
              "teatchers": [
                {
                  "shcool": {
                    "teatchers": [],
                    "schoolName": "Something",
                    "students": [
                      {
                        "school": null,
                        "teatchers": [],
                        "age": 100,
                        "name": "Something",
                        "Companion": null
                      }
                    ]
                  },
                  "students": [],
                  "age": 100,
                  "name": "Something",
                  "Companion": null
                }
              ]
            }
        """.trimIndent())
    }

    fun testHierarchyMultiFiles() {

        // language=Kt
        addClass("""
            package io.nimbly
            interface ISchool {
                companion object {
                    const val id = 123456789L
                }
            }
        """)

        // language=Kt
        addClass("""
            package io.nimbly
            class Person {
                private val name: String? = null
                private val age: Int? = null
            }
        """)

        // language=Kt
        addClass("""
            package io.nimbly
            class Teatcher : Person(), ISchool {
                private val shcool: School? = null
                private val students: Set<Student>? = null
            }
        """)

        // language=Kt
        addClass("""
            package io.nimbly
            class Student : Person(), ISchool {
                private val school: School? = null
                private val teatchers: Set<Teatcher>? = null
            }
        """)

        // language=Kt
        configure("""
            package io.nimbly
            class School {
                private val schoolName: String? = null
                private val students: Set<Student>? = null
                private val teatchers: Set<Teatcher>? = null
            }
            """)

        // language=Json
        assertEquals(toJson2(), """
            {
              "schoolName": "Something",
              "students": [
                {
                  "school": {
                    "teatchers": [
                      {
                        "shcool": null,
                        "students": [],
                        "age": 100,
                        "name": "Something",
                        "Companion": null
                      }
                    ],
                    "schoolName": "Something",
                    "students": []
                  },
                  "teatchers": [],
                  "age": 100,
                  "name": "Something",
                  "Companion": null
                }
              ],
              "teatchers": [
                {
                  "shcool": {
                    "teatchers": [],
                    "schoolName": "Something",
                    "students": [
                      {
                        "school": null,
                        "teatchers": [],
                        "age": 100,
                        "name": "Something",
                        "Companion": null
                      }
                    ]
                  },
                  "students": [],
                  "age": 100,
                  "name": "Something",
                  "Companion": null
                }
              ]
            }
        """.trimIndent())
    }

    fun testFieldRefField() {

        // language=Kt
        configure("""
            package io.nimbly
            class School {
                private val schoolName: String? = null
                private val schoolNameShort = schoolName!!.substring(3)
            }
            """)

        // language=Json
        assertEquals(toJson2(), """
            {
              "schoolName": "Something",
              "schoolNameShort": "Something"
            }
        """.trimIndent())
    }
}