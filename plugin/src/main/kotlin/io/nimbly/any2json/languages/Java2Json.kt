package io.nimbly.any2json.languages

import com.intellij.psi.CommonClassNames
import com.intellij.psi.PsiArrayType
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiEnumConstant
import com.intellij.psi.PsiModifier
import com.intellij.psi.PsiPrimitiveType
import com.intellij.psi.PsiType
import com.intellij.psi.impl.source.PsiClassReferenceType
import com.intellij.psi.util.PsiUtil
import io.nimbly.any2json.AnyToJsonBuilder
import io.nimbly.any2json.EType
import io.nimbly.any2json.EType.SECONDARY
import io.nimbly.any2json.GBoolean
import io.nimbly.any2json.GChar
import io.nimbly.any2json.GDate
import io.nimbly.any2json.GDateTime
import io.nimbly.any2json.GDecimal
import io.nimbly.any2json.GLong
import io.nimbly.any2json.GString
import io.nimbly.any2json.GTime
import io.nimbly.any2json.util.Any2PojoException

class Java2Json(actionType: EType) : AnyToJsonBuilder<PsiClass, Map<String, Any>>(actionType)  {

    @Suppress("UNCHECKED_CAST")
    override fun buildMap(type: PsiClass): Map<String, Any>
        = type.allFields
            .filter { it.modifierList?.hasModifierProperty(PsiModifier.STATIC) == false || type.isInterface }
            .map { it.name to parse(it.type, it.initializer?.text) }
            .filter { it.second != null }
            .toMap() as Map<String, Any>

    private fun parse(
        type: PsiType,
        initializer: String?,
        done: MutableSet<PsiType> = mutableSetOf()
    ): Any? {

        // Primitives
        if (type is PsiPrimitiveType)
            return getValue(type, actionType == SECONDARY, initializer)

        // Primitive array
        if (type is PsiArrayType)
            return listOfNotNull(parse(type.getDeepComponentType(), initializer, done = done))

        // Resolve Psi class
        val psiClass = PsiUtil.resolveClassInClassTypeOnly(type)
            ?: (if (type is PsiClassReferenceType)
                type.resolve() else null)
            ?: return mapOf<String, Any?>()

        // Enum
        if (psiClass.isEnum)
            return psiClass.fields.find { it is PsiEnumConstant }?.name ?: ""

        // Known object with generator
        val names = mutableListOf<String>()
        names += type.presentableText
        names += type.superTypes.map { it.presentableText }
        names.find { GENERATORS[it] != null }?.let {
            return getValue(it, actionType == SECONDARY, initializer)
        }

        // Collections, iterables, arrays, etc.
        if (names.find { it.startsWith("Collection")
                    || it.startsWith("Array")
                    || it.startsWith("Iterable")
                    || it.startsWith("Iterator")
                    || it.startsWith("List") } != null) {
            val parameterType = PsiUtil.extractIterableTypeParameter(type, false)
                ?: PsiUtil.substituteTypeParameter(type, CommonClassNames.JAVA_UTIL_ITERATOR, 0, true)
                ?: PsiUtil.substituteTypeParameter(type, CommonClassNames.JAVA_UTIL_LIST, 0, true)
                ?: return null
            if (parameterType.presentableText == "Object")
                return listOf<Int>()
            return listOfNotNull(parse(parameterType, null, done = done))
        }

        // Prevent stack overflow
        if (done.contains(type))
            return null
        done.add(type)

        // Recurse
        return psiClass.allFields.map {
            it.name to parse(
                it.type,
                it.initializer?.text,
                done = done) }.toMap()
    }

    private fun getValue(type: String, generateValues: Boolean, initializer: String?)
        = GENERATORS[type]!!.generate(generateValues, initializer)

    private fun getValue(type: PsiType, generateValues: Boolean, initializer: String?)
        = when (type.canonicalText) {
            "boolean" -> getValue("Boolean", generateValues, initializer)
            "int", "long", "byte", "short" -> getValue("Number", generateValues, initializer)
            "float" -> getValue("Float", generateValues, initializer)
            "double" -> getValue("Double", generateValues, initializer)
            "char" -> getValue("Character", generateValues, initializer)
            else -> throw Any2PojoException("Not supported primitive '$type.canonicalText'")
        }

    override fun presentation()
        = "from Class" + if (actionType == SECONDARY) " with Data" else ""

    override fun isVisible() = true

    companion object {
        val GENERATORS = mapOf(
            "Boolean" to GBoolean(),
            "Character" to GChar(),
            "CharSequence" to GString(),
            "Long" to GLong(),
            "Number" to GLong(),
            "Double" to GDecimal(1), "Float" to GDecimal(6), "BigDecimal" to GDecimal(12),
            "Date" to GDateTime(), "LocalDateTime" to GDateTime(),
            "LocalDate" to GDate(),
            "LocalTime" to GTime()
        )
    }
}