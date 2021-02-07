package io.nimbly.any2json.generator

import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class GString : Generator<String>() {
    override fun generate(feed: Boolean, initializer: String?): String
        = if (initializer!=null) initializer.substringAfter("\"").substringBeforeLast("\"")
            else if (feed) "Something" else ""
}

class GInteger : Generator<Int>() {
    override fun generate(feed: Boolean, initializer: String?): Int
        = if (initializer!=null) initializer.toIntOrNull() ?: 0
            else if (feed) 100 else 0
}

class GDecimal(val digits: Int) : Generator<BigDecimal>() {
    override fun generate(feed: Boolean, initializer: String?): BigDecimal {
        if (initializer != null) {
            val tbd = initializer.toFloatOrNull()?.toBigDecimal()
            if (tbd != null)
                return tbd
        }
        return BigDecimal.valueOf(
            if (feed) (random.nextFloat() * 100L).toDouble() * 1000000
            else 0.0
        ).setScale(digits, RoundingMode.DOWN)
    }
}

class GChar : Generator<Char>() {
    override fun generate(feed: Boolean, initializer: String?): Char {
        if (initializer!=null) {
            val sub = initializer.substringAfter("'").substringBeforeLast("'")
            if (sub.length > 0) return sub[0].toChar()
        }
        return if (feed) (random.nextInt(26) + 97).toChar() else 'a'
    }
}

class GBoolean : Generator<Boolean>() {
    override fun generate(feed: Boolean, initializer: String?): Boolean
        = if (feed) random.nextBoolean() else false
}

class GDate : GeneratorFormated() {
    private val format = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    override fun generate(feed: Boolean, initializer: String?): String = generate(format)
}

class GTime : GeneratorFormated() {
    private val format = DateTimeFormatter.ofPattern("HH:mm:ss")
    override fun generate(feed: Boolean, initializer: String?): String = generate(format)
}

class GDateTime : GeneratorFormated() {
    private val format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    override fun generate(feed: Boolean, initializer: String?): String = generate(format)
}

abstract class Generator<T> {
    val random = Random()
    abstract fun generate(feed: Boolean, initializer: String?): T
}

abstract class GeneratorFormated : Generator<String>() {
    protected fun generate(format: DateTimeFormatter)
        = LocalDateTime.now().format(format)
}
