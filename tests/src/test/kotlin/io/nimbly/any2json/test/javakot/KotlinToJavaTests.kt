package io.nimbly.any2json.test.javakot

import io.nimbly.any2json.test.AbstractTestCase.EXT.kt
import io.nimbly.any2json.test.AbstractTestCase.EXT.java

class KotlinToJavaTests : AbstractJavaKotlinTestCase() {

    fun testKotlin2JavaEnum() {

        // language=Java
        addClass(java, """
            package io.nimbly.java.sub;
            public enum EGender {MALE, FEMALE, OTHER}
            """)

        // language=Kt
        configure(kt, """
            package io.nimbly.kt
            import io.nimbly.java.sub.EGender
            open class Person {
                private val name = "Nobody"
                private val age: Int? = null
                private val gender: EGender? = null
            }
            """)

        // language=Json
        assertEquals(toJson2(), """
            {
              "name": "Nobody",
              "age": 100,
              "gender": "MALE"
            }
        """.trimIndent())
    }

    fun testKotlin2JavaBug1() {

        // language=Java
        addClass(java, """
            package io.nimbly.java;
            import java.util.Set;
            public class Student { 
                private String studentName;
                private Set<Teatcher> teatchers;
            }
            """)

        // language=Java
        addClass(java, """
            package io.nimbly.java;
            import java.util.Set;
            public class Teatcher {
                private String teatcherName;
                private Set<Student> students;
            }
            """)

        // language=Kt
        configure(kt, """
            package io.nimbly.kt
            import io.nimbly.java.Student
            class Truc {
                private val students: Student? = null
            }
            """)

        // language=Json
        assertEquals(toJson2(), """
            {
              "students": {
                "studentName": "Something",
                "teatchers": [
                  {
                    "teatcherName": "Something",
                    "students": [
                      {
                        "studentName": "Something",
                        "teatchers": []
                      }
                    ]
                  }
                ]
              }
            }
        """.trimIndent())
    }

    fun testKotlin2Java() {

        // language=Java
        addClass(java, """
            package io.nimbly.java.sub;
            public interface ISchool {
                Long id = 123456789L;
            }
            """)

        // language=Java
        addClass(java, """
            package io.nimbly.java.sub;
            public enum EGender {MALE, FEMALE, OTHER}
            """)

        // language=Java
        addClass(java, """
            package io.nimbly.java;
            import io.nimbly.kt.Person;
            import io.nimbly.java.sub.ISchool;
            import io.nimbly.kt.School;
            import java.util.Set;
            public class Student extends Person implements ISchool {
                private School school;
                private Set<Teatcher> teatchers;
            }
            """)

        // language=Java
        addClass(java, """
            package io.nimbly.java;
            import io.nimbly.java.sub.ISchool;
            import io.nimbly.kt.Person;
            import io.nimbly.kt.School;
            
            import java.util.Set;
            public class Teatcher extends Person implements ISchool {
                private School shcool;
                private Set<Student> students;
            }
            """)

        // language=Kt
        addClass(kt, """
            package io.nimbly.kt
            import io.nimbly.java.sub.EGender
            open class Person {
                private val name = "Nobody"
                private val age: Int? = null
                private val gender: EGender? = null
            }
            """)

        // language=Kt
        configure(kt, """
            package io.nimbly.kt
            import io.nimbly.java.Student
            abstract class School {
                private val schoolName: String? = null
                private val schoolNameShort = schoolName!!.substring(3)
                var allStudents: List<Student>? = mutableListOf()
            }
            """)

        // language=Json
        assertEquals(toJson2(), """
            {
              "schoolName": "Something",
              "schoolNameShort": "Something",
              "allStudents": [
                {
                  "school": {
                    "schoolNameShort": "Something",
                    "allStudents": [],
                    "schoolName": "Something"
                  },
                  "teatchers": [
                    {
                      "shcool": null,
                      "students": [],
                      "gender": "MALE",
                      "age": 100,
                      "name": "Something"
                    }
                  ],
                  "gender": "MALE",
                  "age": 100,
                  "name": "Something"
                }
              ]
            }
        """.trimIndent())
    }
}