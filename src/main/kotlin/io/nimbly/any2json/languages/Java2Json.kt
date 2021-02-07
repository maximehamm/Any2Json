package io.nimbly.any2json.languages

import com.intellij.psi.CommonClassNames
import com.intellij.psi.PsiArrayType
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiEnumConstant
import com.intellij.psi.PsiModifier
import com.intellij.psi.PsiPrimitiveType
import com.intellij.psi.PsiType
import com.intellij.psi.util.PsiUtil
import io.nimbly.any2json.AnyToJsonBuilder
import io.nimbly.any2json.generator.GBoolean
import io.nimbly.any2json.generator.GChar
import io.nimbly.any2json.generator.GDate
import io.nimbly.any2json.generator.GDateTime
import io.nimbly.any2json.generator.GDecimal
import io.nimbly.any2json.generator.GInteger
import io.nimbly.any2json.generator.GString
import io.nimbly.any2json.generator.GTime
import io.nimbly.any2json.util.Any2PojoException

class Java2Json(val psiClass: PsiClass, val generateValues: Boolean) : AnyToJsonBuilder() {

    override fun buildMap(): Map<String, Any>
        = psiClass.allFields
            .filter { it.modifierList?.hasModifierProperty(PsiModifier.STATIC) == false }
            .map { it.name to parse(it.type, it.initializer?.text) }
            .filter { it.second != null }.toMap() as Map<String, Any>

    private fun parse(type: PsiType, initializer: String?, done: MutableSet<PsiType> = mutableSetOf()): Any? {

        if (done.contains(type))
            return null

        done.add(type)

        // Primitives
        if (type is PsiPrimitiveType)
            return getValue(type, initializer)

        // Primitive array
        if (type is PsiArrayType)
            return listOfNotNull(parse(type.getDeepComponentType(), initializer, done = done))

        // Enum
        val psiClass = PsiUtil.resolveClassInClassTypeOnly(type) ?: return mapOf<String, Any?>()
        if (psiClass.isEnum)
            return psiClass.fields.find { it is PsiEnumConstant }?.name ?: ""

        // Collections, iterables, arrays, etc.
        val names = mutableListOf<String>()
        names += type.presentableText
        names += type.superTypes.map { it.presentableText }
        if (names.find { it.startsWith("Collection")
                    || it.startsWith("Iterable")
                    || it.startsWith("Iterable")
                    || it.startsWith("List") } != null) {
            val parameterType = PsiUtil.extractIterableTypeParameter(type, false)
                ?: PsiUtil.substituteTypeParameter(type, CommonClassNames.JAVA_UTIL_ITERATOR, 0, true)
                ?: PsiUtil.substituteTypeParameter(type, CommonClassNames.JAVA_UTIL_LIST, 0, true)
                ?: return null
            return listOfNotNull(parse(parameterType, null, done = done))
        }

        // Known object with generator
        names.find { GENERATORS[it] != null }?.let {
            return getValue(it, initializer)
        }

        // Recurse
        return psiClass.allFields.map {
            it.name to parse(
                it.type,
                it.initializer?.text,
                done = done) }.toMap()
    }

    private fun getValue(type: String, initializer: String?)
        = GENERATORS[type]!!.generate(generateValues, initializer)

    private fun getValue(type: PsiType, initializer: String?)
        = when (type.canonicalText) {
            "boolean" -> getValue("Boolean", initializer)
            "int", "long", "byte", "short" -> getValue("Number", initializer)
            "float", "double" -> getValue("BigDecimal", initializer)
            "char" -> getValue("Character", initializer)
            else -> throw Any2PojoException("Not supported primitive '$type.canonicalText'")
        }

    companion object {
        val GENERATORS = mapOf(
            "Boolean" to GBoolean(),
            "Character" to GChar(),
            "CharSequence" to GString(),
            "Number" to GInteger(),
            "Double" to GDecimal(2), "Float" to GDecimal(6), "BigDecimal" to GDecimal(12),
            "Date" to GDateTime(), "LocalDateTime" to GDateTime(),
            "LocalDate" to GDate(),
            "LocalTime" to GTime())
    }
}