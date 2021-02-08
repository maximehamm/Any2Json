package io.nimbly.test.any2json.javakot

import io.nimbly.test.any2json.AbstractTestCase.EXT.java
import io.nimbly.test.any2json.AbstractTestCase.EXT.kt

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
        assertEquals(toJsonRandom(), """
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
        assertEquals(toJsonRandom(), """
            {
              "students": {
                "studentName": "Something",
                "teatchers": [
                  {
                    "teatcherName": "Something",
                    "students": [
                      {
                        "studentName": "Something"
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
                abstract var students: List<Student>?
            }
            """)

        // language=Json
        assertEquals(toJsonRandom(), """
            {
              "schoolName": "Something",
              "schoolNameShort": "Something",
              "students": [
                {
                  "school": {
                    "schoolNameShort": "Something",
                    "schoolName": "Something"
                  },
                  "teatchers": [
                    {
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