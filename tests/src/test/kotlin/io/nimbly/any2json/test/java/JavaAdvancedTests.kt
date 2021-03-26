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

package io.nimbly.any2json.test.java

import io.nimbly.any2json.test.AbstractJavaTestCase

class JavaAdvancedTests : AbstractJavaTestCase() {

    fun testEnum() {

        // language=Java
        configure("""
            package io.nimbly;
            enum EGender { MALE, FEMALE, OTHER }
            class Person {
                private String name;
                private Integer age;
                private EGender gender;
            }           
            """)

        // language=Json
        assertEquals(copy(), """
            {
              "name": "Something",
              "age": 100,
              "gender": "MALE"
            }
        """.trimIndent())
    }

    fun testArraysSimple() {

        // language=Java
        configure("""
            package io.nimbly;
            import java.util.Set;
            class School {
                private String schoolName;
                private Set<Student> students;
            }
            class Student extends Person implements ISchool {
                private School school;
            }
            public class Person {
                private String name;
                private Integer age;
            }
            interface ISchool {
                Long id = 123456789L;
            }                
            """)

        // language=Json
        assertEquals(copy(), """
            {
              "schoolName": "Something",
              "students": [
                {
                  "school": {
                    "schoolName": "Something",
                    "students": []
                  },
                  "name": "Something",
                  "age": 100,
                  "id": 123456789
                }
              ]
            }
        """.trimIndent())
    }

    fun testArraysBold() {

        // language=Java
        configure(
            """
            package io.nimbly;
            
            import java.util.Set;
            
            class School {
                private String schoolName;
                private Set<Student> students;
                private Set<Teatcher> teatchers;
            }
            
            class Student extends Person implements ISchool {
                private School school;
                private  Set<Teatcher> teatchers;
            }
            
            class Teatcher extends Person implements ISchool {
                private School shcool;
                private Set<Student> students;
            }
            
            public class Person {
                private String name;
                private Integer age;
            }
            
            interface ISchool {
                Long id = 123456789L;
            }    
            """
        )

        // language=Json
        assertEquals(copy(), """
            {
              "schoolName": "Something",
              "students": [
                {
                  "school": {
                    "schoolName": "Something",
                    "students": [],
                    "teatchers": [
                      {
                        "shcool": null,
                        "students": [],
                        "name": "Something",
                        "age": 100,
                        "id": 123456789
                      }
                    ]
                  },
                  "teatchers": [],
                  "name": "Something",
                  "age": 100,
                  "id": 123456789
                }
              ],
              "teatchers": [
                {
                  "shcool": {
                    "schoolName": "Something",
                    "students": [
                      {
                        "school": null,
                        "teatchers": [],
                        "name": "Something",
                        "age": 100,
                        "id": 123456789
                      }
                    ],
                    "teatchers": []
                  },
                  "students": [],
                  "name": "Something",
                  "age": 100,
                  "id": 123456789
                }
              ]
            }
        """.trimIndent())
    }

    fun testHierarchyMultiFiles() {

        // language=Java
        addClass("""
            package io.nimbly;
            interface ISchool {
                java.lang.Long id = 123456789L;
            }  
        """)

        // language=Java
        addClass("""
            package io.nimbly;
            public class Person {
                private java.lang.String name;
                private java.lang.Integer age;
            }
        """)

        // language=Java
        addClass("""
            package io.nimbly;
            import java.util.Set;
            class Teatcher extends Person implements ISchool {
                private School shcool;
                private Set<Student> students;
            }
        """)

        // language=Java
        addClass("""
            package io.nimbly;
            import java.util.Set;
            class Student extends Person implements ISchool {
                private School school;
                private Set<Teatcher> teatchers;
            }
        """)

        // language=Java
        configure("""
            package io.nimbly;
            import java.util.Set;
            class School {
                private java.lang.String schoolName;
                private Set<Student> students;
                private Set<Teatcher> teatchers;
            }
            """)

        // language=Json
        assertEquals(copy(), """
        {
          "schoolName": "Something",
          "students": [
            {
              "school": {
                "schoolName": "Something",
                "students": [],
                "teatchers": [
                  {
                    "shcool": null,
                    "students": [],
                    "name": "Something",
                    "age": 100,
                    "id": 123456789
                  }
                ]
              },
              "teatchers": [],
              "name": "Something",
              "age": 100,
              "id": 123456789
            }
          ],
          "teatchers": [
            {
              "shcool": {
                "schoolName": "Something",
                "students": [
                  {
                    "school": null,
                    "teatchers": [],
                    "name": "Something",
                    "age": 100,
                    "id": 123456789
                  }
                ],
                "teatchers": []
              },
              "students": [],
              "name": "Something",
              "age": 100,
              "id": 123456789
            }
          ]
        }
        """.trimIndent())
    }

    fun testFieldRefField() {

        // language=Java
        configure("""
            package io.nimbly;
            class School {
                private String schoolName;
                private String schoolNameShort = schoolName.substring(3);
            }
            """)

        // language=Json
        assertEquals(copy(), """
            {
              "schoolName": "Something",
              "schoolNameShort": "Something"
            }
        """.trimIndent())
    }
}