/*
 * ANY2JSON
 * Copyright (C) 2024  Maxime HAMM - NIMBLY CONSULTING - maxime.hamm.pro@gmail.com
 *
 * This document is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This work is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package io.nimbly.any2json

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.psi.util.PsiTreeUtil
import io.nimbly.any2json.EAction.COPY
import io.nimbly.any2json.EAction.PREVIEW
import io.nimbly.any2json.conversion.toJson
import io.nimbly.any2json.util.processAction
import org.jetbrains.kotlin.incremental.KotlinLookupLocation
import org.jetbrains.kotlin.incremental.components.LookupLocation
import org.jetbrains.kotlin.js.descriptorUtils.nameIfStandardType
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.nj2k.postProcessing.type
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.types.DeferredType
import org.jetbrains.kotlin.types.FlexibleType
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.SimpleType
import org.jetbrains.kotlin.types.typeUtil.isEnum
import org.jetbrains.kotlin.types.typeUtil.supertypes

class Kotlin2JsonGeneratePreview : AbstractKotlin2JsonGenerate(PREVIEW), Any2JsonPreviewExtensionPoint

class Kotlin2JsonGenerateCopy : AbstractKotlin2JsonGenerate(COPY), Any2JsonCopyExtensionPoint

abstract class AbstractKotlin2JsonGenerate(private val action: EAction) : Any2JsonRootExtensionPoint {

    override fun process(event: AnActionEvent): Boolean {

        val editor = event.getData(CommonDataKeys.EDITOR) ?: return false
        val project = event.project ?: return false
        val psiFile = event.getData(CommonDataKeys.PSI_FILE) ?: return false
        val element = psiFile.findElementAt(editor.caretModel.offset)

        val type = PsiTreeUtil.getContextOfType(element, KtClass::class.java)
            ?: return false

        val map = type.getProperties()
            .map { it.name to parse(
                it.type(),
                it.initializer?.text,
                KotlinLookupLocation(type))}
            .filter { it.second != null }
            .toMap()

        val json = toJson(map)
        return processAction(action, json, project, event.dataContext)
    }

    @Suppress("NAME_SHADOWING")
    private fun parse(
        type: KotlinType?,
        initializer: String?,
        lookupLocation: LookupLocation,
        actionType: EType = EType.SECONDARY,
        done: MutableSet<KotlinType> = mutableSetOf()
    ): Any? {

        val initializer = if (initializer == "null") null else initializer

        var type = type
        if (type == null)
            return null

        if (type is DeferredType)
            type = type.unwrap()

        if (type is FlexibleType)
            type = type.delegate

        // Enum
        if (type is SimpleType && type.isEnum()) {
            val names = mutableListOf<Name>()
            type.memberScope.getClassifierNames()?.let { names.addAll(it) }
            type.memberScope.getVariableNames().let { names.addAll(it) }
            return names
                .map { it.identifier }
                .firstOrNull { it != "name" && it != "ordinal" && it != "Companion" }
        }

        // Known object with generator
        val typeName = type.nameIfStandardType?.identifier
            ?: type.toString().substringBeforeLast("?")
        GENERATORS[typeName]?.let {
            return it.generate(actionType == EType.SECONDARY, initializer)
        }

        // Collections, iterables, arrays, etc.
        val names = mutableListOf<String>()
        names += typeName.substringAfterLast(".")
        names += type.supertypes()
            .map { it.nameIfStandardType?.identifier ?: it.toString() }
            .map { it.substringAfterLast(".")}
        if (names.find { it.startsWith("Collection")
                || it.startsWith("Array")
                || it.startsWith("Iterable")
                || it.startsWith("Iterator")
                || it.startsWith("List") } != null) {
            val parameterType = type.arguments.first().type
            if (parameterType.toString().substringBeforeLast("?") == "Any")
                return listOf<Int>()
            return listOfNotNull(parse(parameterType, null, lookupLocation, actionType, done = done))
        }

        // Avoid stack overflow
        if (done.contains(type))
            return null
        done.add(type)

        // Recurse
        return type.memberScope.getVariableNames().map {
            it.identifier to parse(
                type.memberScope.getContributedVariables(it, lookupLocation).firstOrNull()?.returnType,
                type.memberScope.getContributedVariables(it, lookupLocation).firstOrNull()?.compileTimeInitializer?.value.toString(),
                lookupLocation, actionType, done)
        }.toMap()
    }

    override fun isEnabled(event: AnActionEvent): Boolean {
        val psiFile = event.getData(CommonDataKeys.PSI_FILE) ?: return false
        if (!psiFile.name.endsWith(".kt"))
            return false

        val editor = event.getData(CommonDataKeys.EDITOR) ?:
        return false

        val element = psiFile.findElementAt(editor.caretModel.offset)
            ?: return false

        val type = PsiTreeUtil.getContextOfType(element, KtClass::class.java)
        return type!=null
            && element.parent == type
    }

    override fun isVisible(event: AnActionEvent)
        = isEnabled(event)

    override fun presentation(event: AnActionEvent): String {
        val psiFile = event.getData(CommonDataKeys.PSI_FILE)!!
        val editor = event.getData(CommonDataKeys.EDITOR)!!
        val element = psiFile.findElementAt(editor.caretModel.offset)!!
        val type = PsiTreeUtil.getContextOfType(element, KtClass::class.java)!!
        return if (COPY == action)
                "Copy Json sample from ${type.name}"
            else
                "Preview Json sample from ${type.name}"
    }

    companion object {
        val GENERATORS = mapOf(
            "Boolean" to GBoolean(),
            "Int" to GInteger(), "Long" to GLong(),
            "Number" to GInteger(),
            "Char" to GChar(),
            "CharSequence" to GString(), "String" to GString(),
            "Double" to GDecimal(1), "Float" to GDecimal(6), "BigDecimal" to GDecimal(12),
            "Date" to GDateTime(), "LocalDateTime" to GDateTime(),
            "LocalDate" to GDate(),
            "LocalTime" to GTime(),
        )
    }
}

