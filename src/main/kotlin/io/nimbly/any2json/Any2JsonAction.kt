package io.nimbly.any2json

import com.google.gson.GsonBuilder
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.xml.XmlTag
import com.intellij.xdebugger.impl.ui.tree.XDebuggerTree
import com.intellij.xdebugger.impl.ui.tree.nodes.XValueNodeImpl
import io.nimbly.any2json.EType.MAIN
import io.nimbly.any2json.EType.SECONDARY
import io.nimbly.any2json.debugger.Variable2Json
import io.nimbly.any2json.languages.Csv2Json
import io.nimbly.any2json.languages.Java2Json
import io.nimbly.any2json.languages.Kotlin2Json
import io.nimbly.any2json.languages.Properties2Json
import io.nimbly.any2json.languages.Xml2Json
import io.nimbly.any2json.languages.Yaml2Json
import io.nimbly.any2json.util.Any2PojoException
import io.nimbly.any2json.util.error
import io.nimbly.any2json.util.info
import io.nimbly.any2json.util.warn
import io.nimbly.extension.ANY2JSON
import org.jetbrains.kotlin.psi.KtClass
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

class Any2JsonDefaultAction : Any2JsonAction(MAIN)

class Any2JsonRandomAction : Any2JsonAction(SECONDARY)

abstract class Any2JsonAction(private val type: EType): AnAction() { //DebuggerAction()

    override fun actionPerformed(e: AnActionEvent) {

        val project = e.project!!

        try {

            val editor = e.getData(CommonDataKeys.EDITOR)
            val psiFile = e.getData(CommonDataKeys.PSI_FILE)

            // Try community languages
            var result: Pair<String, Any>? = null
            if (psiFile != null && editor!=null) {
                val element = psiFile.findElementAt(editor.caretModel.offset)

                result = buildFromJava(element)
                      ?: buildFromKotlin(element)
                      ?: buildFromXml(element)
                      ?: buildFromCsv(psiFile)
                      ?: buildFromYaml(psiFile)
                      ?: buildFromProperties(psiFile)
            }

            // Try using extensions
            if (result == null) {
                ANY2JSON().extensionList.forEach {
                    result = it.build(e)
                    if (result != null)
                        return@forEach
                }
            }

            // Should be the debugger impl
            if (result == null && psiFile == null) {
                result = buildDebugger(e)
            }

            // Oups !?
            if (result == null)
                throw Any2PojoException("Unable to define context !")

            // Convert to Json
            val json = GsonBuilder().setPrettyPrinting().create().toJson(result!!.second)

            // Put to clipboard
            Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(json), StringSelection(json))

            // Report notificagton
            info("${result!!.first} to JSON copied to clipboard !", project)

        } catch (ex: Any2PojoException) {
            warn(ex.message!!, project)
        } catch (ex: Exception) {
            ex.printStackTrace()
            error("Any to JSON error !", project)
        }
    }

    private fun buildDebugger(e: AnActionEvent): Pair<String, Map<String, Any>>? {
        val xtree: XDebuggerTree = e.dataContext.getData("xdebugger.tree") as XDebuggerTree?
            ?: return null
        val xpath = xtree.selectionPath?.lastPathComponent
        if (xpath !is XValueNodeImpl)
            return null
        val xnode: XValueNodeImpl = xpath
        return Pair(xnode.name!!,
            Variable2Json(type).buildMap(xnode))
    }

    private fun buildFromJava(element: PsiElement?): Pair<String, Map<String, Any>>? {
        val psiClass = PsiTreeUtil.getContextOfType(element, PsiClass::class.java)
            ?: return null
        return Pair(psiClass.name!!,
            Java2Json(type).buildMap(psiClass))
    }

    private fun buildFromKotlin(element: PsiElement?): Pair<String, Map<String, Any>>? {
        val ktClass = PsiTreeUtil.getContextOfType(element, KtClass::class.java)
            ?: return null
        return Pair(ktClass.name!!,
            Kotlin2Json(type).buildMap(ktClass))
    }

    private fun buildFromXml(element: PsiElement?): Pair<String, Map<String, Any>>? {
        val xmlTag = PsiTreeUtil.getContextOfType(element, XmlTag::class.java)
            ?: return null
        return Pair(xmlTag.name,
            Xml2Json(type).buildMap(xmlTag))
    }

    private fun buildFromCsv(element: PsiElement): Pair<String, List<Map<String, Any>>>? {
        if (!element.containingFile.name.toLowerCase().endsWith("csv"))
            return null
        return Pair("CSV",
            Csv2Json(type).buildMap(element.containingFile.text))
    }

    private fun buildFromYaml(element: PsiElement): Pair<String, List<Map<String, Any>>>? {
        if (!element.containingFile.name.toLowerCase().endsWith("yaml")
            && !element.containingFile.name.toLowerCase().endsWith("yml"))
            return null
        return Pair("YAML",
            Yaml2Json(type).buildMap(element.containingFile.text))
    }

    private fun buildFromProperties(element: PsiElement): Pair<String, Any>? {
        if (!element.containingFile.name.toLowerCase().endsWith("properties"))
            return null
        return Pair("PROPERTIES",
            Properties2Json(type).buildMap(element.containingFile.text))
    }

    override fun update(e: AnActionEvent) {

        val editor = e.getData(CommonDataKeys.EDITOR)
        val psiFile = e.getData(CommonDataKeys.PSI_FILE)
        val any2Json: AnyToJsonBuilder<out Any, out Any>? =
            if (psiFile != null && editor != null) {
                val element = psiFile.findElementAt(editor.caretModel.offset)
                if (PsiTreeUtil.getContextOfType(element, PsiClass::class.java) !=null) {
                    Java2Json(type)
                }
                else if (PsiTreeUtil.getContextOfType(element, KtClass::class.java) !=null) {
                    Kotlin2Json(type)
                }
                else if (PsiTreeUtil.getContextOfType(element, XmlTag::class.java) !=null) {
                    Xml2Json(type)
                }
                else {
                    val fileName = psiFile.name.toLowerCase()
                    if (fileName.endsWith(".csv")) {
                        Csv2Json(type)
                    }
                    else if (fileName.endsWith(".yaml") || fileName.endsWith(".yml")) {
                        Yaml2Json(type)
                    }
                    else if (fileName.endsWith(".properties")) {
                        Properties2Json(type)
                    }
                    else {
                        null
                    }
                }
            }
            else {
                Variable2Json(type)
            }

        if (any2Json != null) {
            e.presentation.text = "Generate JSON " + any2Json.presentation()
            e.presentation.isVisible = any2Json.isVisible()
            e.presentation.isEnabled = true
            return
        }

        ANY2JSON().extensionList.forEach { ext ->
            if (ext.isEnabled(e, type)) {
                e.presentation.text = "Generate JSON " + ext.presentation()
                e.presentation.isVisible = true
                e.presentation.isEnabled = true
                return
            }
        }

        e.presentation.isVisible = false
        e.presentation.isEnabled = false
    }
}

enum class EType { MAIN, SECONDARY }

abstract class AnyToJsonBuilder<T:Any, J:Any>(
        val actionType: EType) {

    abstract fun buildMap(type: T): J
    abstract fun presentation(): String
    abstract fun isVisible(): Boolean
}