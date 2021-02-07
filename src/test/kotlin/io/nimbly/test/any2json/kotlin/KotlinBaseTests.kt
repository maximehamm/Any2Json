package io.nimbly.test.any2json.kotlin

class KotlinBaseTests : AbstractKotlinTestCase() {

    fun testPrimitivesWithValues() {

        // language=Kt
        configure("""
            package io.nimbly
            open class Person {
                val zeBoolean = false
                val zeInt = 66
                val zeLong = 66666
                val zeDouble = 66.666
                val zeFloat = 6666666666f
                val zeChar = 'u'
            }""")

        // language=Json
        assertEquals(toJson(), """
            {
              "zeBoolean": false,
              "zeInt": 66,
              "zeLong": 66666,
              "zeDouble": 66.666,
              "zeFloat": 6.6666665E+9,
              "zeChar": "u"
            }
        """.trimIndent())
    }

    fun testPrimitivesWithDefault() {

        // language=Kt
        configure("""
            package io.nimbly
            open class Person {
                val zeBoolean: Boolean = false
                val zeInt: Int = 66
                val zeLong: Long = 66666
                val zeDouble: Double = 66.666
                val zeFloat: Float = 6666666666f
                val zeChar: Char = 'u'
            }""")

        // language=Json
        assertEquals(toJson(), """
            {
              "zeBoolean": false,
              "zeInt": 66,
              "zeLong": 66666,
              "zeDouble": 66.666,
              "zeFloat": 6.6666665E+9,
              "zeChar": "u"
            }
        """.trimIndent())
    }

    fun testPrimitivesWithoutValues() {

        // language=Kt
        configure("""
            package io.nimbly
            abstract class Person {
                abstract val zeBoolean: Boolean
                abstract val zeInt: Int
                abstract val zeLong: Long
                abstract val zeDouble: Double
                abstract val zeFloat: Float
                abstract val zeChar: Char
            }""")

        // language=Json
        assertEquals(toJson(), """
            {
              "zeBoolean": false,
              "zeInt": 0,
              "zeLong": 0,
              "zeDouble": 0.0,
              "zeFloat": 0.000000,
              "zeChar": "a"
            }
        """.trimIndent())

        // language=Json
        assertEquals(toJsonRandom(), """
            {
              "zeBoolean": true,
              "zeInt": 100,
              "zeLong": 100,
              "zeDouble": 9999900000.0,
              "zeFloat": 9999900000.000000,
              "zeChar": "w"
            }
        """.trimIndent())
    }

    fun testJavaMath() {

        // language=Kt
        configure(
            """
                package io.nimbly
                import java.math.BigDecimal
                class Person {
                    private val zeBigDecimal: BigDecimal? = null
                }""")

        // language=Json
        assertEquals(toJson(), """
            {
              "zeBigDecimal": 0E-12
            }
        """.trimIndent())
    }

    fun testJavaTime() {

        // language=Kt
        configure("""
                package io.nimbly
                import java.time.LocalDate
                import java.time.LocalDateTime
                import java.time.LocalTime
                import java.util.Date
                class Person {
                    var zeDate: Date? = null
                    var zeLocalDateTime: LocalDateTime? = null
                    var zeLocalDate: LocalDate? = null
                    var zeLocalTime: LocalTime? = null
                }""")

        // language=Json
        assertEquals(toJson(), """
            {
              "zeDate": "2020-03-23 00:00:00",
              "zeLocalDateTime": "2020-03-23 00:00:00",
              "zeLocalDate": "2020-03-23",
              "zeLocalTime": "00:00:00"
            }
        """.trimIndent())
    }

    fun testIgnoreStatic() {

        // language=Kt
        configure("""
            package io.nimbly
            class Person {
                private val name = 0
                companion object {
                    const val TEST = "TEST"
                }
            }""")

        // language=Json
        assertEquals(toJson(), """
            {
              "name": 0
            }""".trimIndent())
    }

    fun testInterface() {

        // language=Kt
        configure("""
            package io.nimbly
            interface ISchool {
                companion object {
                    const val id = 123456789L
                }
            }""")

        // language=Json
        assertEquals(toJsonRandom(), """
            {}
        """.trimIndent())
    }
}