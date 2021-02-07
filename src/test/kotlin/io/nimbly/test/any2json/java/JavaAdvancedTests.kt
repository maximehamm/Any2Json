package io.nimbly.test.any2json.java

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
        assertEquals(toJsonRandom(), """
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
        assertEquals(toJsonRandom(), """
            {
              "schoolName": "Something",
              "students": [
                {
                  "school": {
                    "schoolName": "Something"
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
        assertEquals(toJsonRandom(), """
            {
              "schoolName": "Something",
              "students": [
                {
                  "school": {
                    "schoolName": "Something",
                    "teatchers": [
                      {
                        "name": "Something",
                        "age": 100,
                        "id": 123456789
                      }
                    ]
                  },
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
                        "name": "Something",
                        "age": 100,
                        "id": 123456789
                      }
                    ]
                  },
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
        assertEquals(toJsonRandom(), """
            {
              "schoolName": "Something",
              "students": [
                {
                  "school": {
                    "schoolName": "Something",
                    "teatchers": [
                      {
                        "name": "Something",
                        "age": 100,
                        "id": 123456789
                      }
                    ]
                  },
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
                        "name": "Something",
                        "age": 100,
                        "id": 123456789
                      }
                    ]
                  },
                  "name": "Something",
                  "age": 100,
                  "id": 123456789
                }
              ]
            }
        """.trimIndent())
    }


}