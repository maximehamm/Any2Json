package io.nimbly.any2json.languages

import io.nimbly.any2json.AnyToJsonBuilder
import io.nimbly.any2json.generator.GBoolean
import io.nimbly.any2json.generator.GChar
import io.nimbly.any2json.generator.GDate
import io.nimbly.any2json.generator.GDateTime
import io.nimbly.any2json.generator.GDecimal
import io.nimbly.any2json.generator.GInteger
import io.nimbly.any2json.generator.GString
import io.nimbly.any2json.generator.GTime
import org.jetbrains.kotlin.nj2k.postProcessing.type
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.resolve.calls.callUtil.createLookupLocation
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.SimpleType
import org.jetbrains.kotlin.types.typeUtil.isEnum
import org.jetbrains.kotlin.types.typeUtil.supertypes

class Kotlin2Json(val ktClass: KtClass, val generateValues: Boolean) : AnyToJsonBuilder() {

    override fun buildMap(): Map<String, Any>
        = ktClass.getProperties().map { it.name to
            parse(it.type(), it.initializer?.text) }.filter { it.second != null }.toMap() as Map<String, Any>

    private fun parse(type: KotlinType?, initializer: String?, done: MutableSet<String> = mutableSetOf()): Any? {

        if (type == null)
            return null

        val typeName = type.toString().substringBeforeLast("?")
        if (done.contains(typeName))
            return null
        done.add(typeName)

        // Enum
        if (type is SimpleType && type.isEnum())
            return type.memberScope.getVariableNames().map { it.identifier }.filter { it != "name" && it != "ordinal" }.first()

        // Collections, iterables, arrays, etc.
        val names = mutableListOf<String>()
        names += typeName
        names += type.supertypes().map { it.toString().substringBeforeLast("?") }
        if (names.find { it.startsWith("Collection")
                    || it.startsWith("Iterable")
                    || it.startsWith("Iterable")
                    || it.startsWith("List") } != null) {
            val parameterType = type.arguments.first().type
            return listOfNotNull(parse(parameterType, null, done = done))
        }

        // Known object with generator
        GENERATORS[typeName]?.let {
            return it.generate(generateValues, initializer)
        }

        // Recurse
        val location = ktClass.createLookupLocation()!!
        return type.memberScope.getVariableNames().map {
            it.identifier to parse(
                type.memberScope.getContributedVariables(it, location).firstOrNull()?.returnType,
                type.memberScope.getContributedVariables(it, location).firstOrNull()?.compileTimeInitializer?.value.toString(),
                done = done)
        }.toMap()
    }

    companion object {
        val GENERATORS = mapOf(
            "Boolean" to GBoolean(),
            "Int" to GInteger(), "Number" to GInteger(),
            "Char" to GChar(),
            "CharSequence" to GString(), "String" to GString(),
            "Double" to GDecimal(2), "Float" to GDecimal(6), "BigDecimal" to GDecimal(12),
            "Date" to GDateTime(), "LocalDateTime" to GDateTime(),
            "LocalDate" to GDate(),
            "LocalTime" to GTime(),
        )
    }

}