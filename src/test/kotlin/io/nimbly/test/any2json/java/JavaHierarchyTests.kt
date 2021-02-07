package io.nimbly.test.any2json.java

class JavaHierarchyTests : AbstractJavaTestCase() {

    fun testArraysSimple() {

        configure("""
            package io.nimbly;
            import java.util.Set;
            class School<caret> {
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
                Long id = 123456789l;
            }                
            """)

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

    fun testArraysHeavy() {

        configure("""
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
                Long id = 123456789l;
            }    
            """)

        assertEquals(toJsonRandom(), """
            {
              "schoolName": "Something",
              "students": [
                {
                  "school": {
                    "schoolName": "Something",
                    "teatchers": [
                      {
                        "age": 100,
                        "id": 0
                      }
                    ],
                    "age": 100,
                    "id": 0
                  }
                }
              ],
              "teatchers": [
                {
                  "school": {
                    "schoolName": "Something",
                    "students": [
                      {
                        "age": 100,
                        "id": 0
                      }
                    ]
                  }
                }
              ]
            }
        """.trimIndent())
    }

}