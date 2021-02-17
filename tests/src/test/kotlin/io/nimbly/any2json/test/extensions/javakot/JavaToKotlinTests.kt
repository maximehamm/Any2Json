package io.nimbly.any2json.test.extensions.javakot

import io.nimbly.any2json.test.AbstractTestCase.EXT.kt
import io.nimbly.any2json.test.AbstractTestCase.EXT.java

class JavaToKotlinTests : AbstractJavaKotlinTestCase() {

    fun testJava2Kotlin1() {

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
        addClass(kt, """
            package io.nimbly.kt
            import io.nimbly.java.Student
            abstract class School {
                private val schoolName: String? = null
                private val schoolNameShort = schoolName!!.substring(3)
                var allStudents: List<Student>? = mutableListOf()
            }
            """)
        
        // language=Java
        addClass(java, """
            package io.nimbly.java.sub;
            import java.lang.Long;
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
        configure(java, """
            package io.nimbly.java;
            import io.nimbly.java.sub.ISchool;
            import io.nimbly.kt.Person;
            import io.nimbly.kt.School;
            
            import java.util.Set;
            public class Teatcher extends Person implements ISchool {
                private School school;
                private Set<Student> students;
            }
            """)

        // language=Json
        assertEquals(toJson2(), """
            {
              "school": {
                "schoolName": "Something",
                "schoolNameShort": "Something",
                "allStudents": [
                  {}
                ]
              },
              "students": [
                {
                  "school": {
                    "schoolName": "Something",
                    "schoolNameShort": "Something",
                    "allStudents": [
                      {}
                    ]
                  },
                  "teatchers": [
                    {
                      "school": null,
                      "students": [],
                      "name": "Something",
                      "age": 100,
                      "gender": "MALE",
                      "id": 123456789
                    }
                  ],
                  "name": "Something",
                  "age": 100,
                  "gender": "MALE",
                  "id": 123456789
                }
              ],
              "name": "Something",
              "age": 100,
              "gender": "MALE"
            }
        """.trimIndent())
    }

    fun testJava2Kotlin2() {

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
        addClass(kt, """
            package io.nimbly.kt
            import io.nimbly.java.Student
            import io.nimbly.java.Teatcher
            abstract class School {
                private val schoolName: String? = null
                private val schoolNameShort = schoolName!!.substring(3)
                var allStudents: List<Student>? = mutableListOf()
            }
            """)

        // language=Java
        addClass(java, """
            package io.nimbly.java.sub;
            import java.lang.Long;
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
            import io.nimbly.java.sub.ISchool;
            import io.nimbly.kt.Person;
            import io.nimbly.kt.School;
            
            import java.util.Set;
            public class Teatcher extends Person implements ISchool {
                private School school;
                private Set<Student> students;
            }
            """)

        // language=Java
        configure(java, """
            package io.nimbly.java;
            import io.nimbly.kt.Person;
            import io.nimbly.java.sub.ISchool;
            import io.nimbly.kt.School;
            import java.util.Set;
            public class Student extends Person implements ISchool {
                private School school;
            }
            """)

        // language=Json
        assertEquals(toJson2(), """
            {
              "school": {
                "schoolName": "Something",
                "schoolNameShort": "Something",
                "allStudents": [
                  {}
                ]
              },
              "name": "Something",
              "age": 100,
              "gender": "MALE"
            }
        """.trimIndent())
    }

}