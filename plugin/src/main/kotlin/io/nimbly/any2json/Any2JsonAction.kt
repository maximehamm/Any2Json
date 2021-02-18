package io.nimbly.any2json

import com.google.gson.GsonBuilder
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.xml.XmlTag
import io.nimbly.any2json.EType.MAIN
import io.nimbly.any2json.EType.SECONDARY
import io.nimbly.any2json.debugger.Debugger2Json
import io.nimbly.any2json.languages.Csv2Json
import io.nimbly.any2json.languages.Properties2Json
import io.nimbly.any2json.languages.Xml2Json
import io.nimbly.any2json.languages.Yaml2Json
import io.nimbly.any2json.util.error
import io.nimbly.any2json.util.info
import io.nimbly.any2json.util.warn
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

class Any2JsonDefaultAction : Any2JsonAction(MAIN)

class Any2JsonRandomAction : Any2JsonAction(SECONDARY)

abstract class Any2JsonAction(private val actionType: EType): AnAction() { //DebuggerAction()

    override fun actionPerformed(event: AnActionEvent) {

        val project = event.project!!

        try {

            val editor = event.getData(CommonDataKeys.EDITOR)
            val psiFile = event.getData(CommonDataKeys.PSI_FILE)

            // Try community languages
            var result: Pair<String, Any>? = null
            if (psiFile != null && editor!=null) {
                val element = psiFile.findElementAt(editor.caretModel.offset)

                result =
                      buildFromXml(element)
                      ?: buildFromCsv(psiFile)
                      ?: buildFromYaml(psiFile)
                      ?: buildFromProperties(psiFile)
            }

            // Try using extensions
            if (result == null) {
                ANY2JSON().extensionList.find {
                    result = it.build(event, actionType)
                    result != null
                }
            }

            // Try debugger
            if (result == null) {
                result = Debugger2Json().build(event)
            }

            // Oups !?
            if (result == null)
                throw Any2PojoException("Unable to define context !")

            // Convert to Json
            val json = GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .disableHtmlEscaping()
                .create()
                .toJson(result!!.second)

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

    private fun buildFromXml(element: PsiElement?): Pair<String, Map<String, Any>>? {
        val xmlTag = PsiTreeUtil.getContextOfType(element, XmlTag::class.java)
            ?: return null
        return Pair(xmlTag.name,
            Xml2Json(actionType).buildMap(xmlTag))
    }

    private fun buildFromCsv(element: PsiElement): Pair<String, List<Map<String, Any>>>? {
        if (!element.containingFile.name.toLowerCase().endsWith("csv"))
            return null
        return Pair("CSV",
            Csv2Json(actionType).buildMap(element.containingFile.text))
    }

    private fun buildFromYaml(element: PsiElement): Pair<String, List<Map<String, Any>>>? {
        if (!element.containingFile.name.toLowerCase().endsWith("yaml")
            && !element.containingFile.name.toLowerCase().endsWith("yml"))
            return null
        return Pair("YAML",
            Yaml2Json(actionType).buildMap(element.containingFile.text))
    }

    private fun buildFromProperties(element: PsiElement): Pair<String, Any>? {
        if (!element.containingFile.name.toLowerCase().endsWith("properties"))
            return null
        return Pair("PROPERTIES",
            Properties2Json(actionType).buildMap(element.containingFile.text))
    }

    private fun buildFromDebugger(element: PsiElement): Pair<String, Any>? {
        if (!element.containingFile.name.toLowerCase().endsWith("properties"))
            return null
        return Pair("PROPERTIES",
            Properties2Json(actionType).buildMap(element.containingFile.text))
    }

    override fun update(event: AnActionEvent) {

        val editor = event.getData(CommonDataKeys.EDITOR)
        val psiFile = event.getData(CommonDataKeys.PSI_FILE)
        var any2Json: AnyToJsonBuilder<out Any, out Any>? = null

        if (psiFile != null && editor != null) {
            val element = psiFile.findElementAt(editor.caretModel.offset)
            if (PsiTreeUtil.getContextOfType(element, XmlTag::class.java) != null) {
                any2Json = Xml2Json(actionType)
            } else {
                val fileName = psiFile.name.toLowerCase()
                if (fileName.endsWith(".csv")) {
                    any2Json = Csv2Json(actionType)
                } else if (fileName.endsWith(".yaml") || fileName.endsWith(".yml")) {
                    any2Json = Yaml2Json(actionType)
                } else if (fileName.endsWith(".properties")) {
                    any2Json = Properties2Json(actionType)
                }
            }
        }

        if (any2Json != null) {
            event.presentation.text = "Generate JSON " + any2Json.presentation()
            event.presentation.isVisible = any2Json.isVisible()
            event.presentation.isEnabled = true
            return
        }

        if (Debugger2Json().isVisible(event, actionType)) {
            event.presentation.text = "Generate JSON"
            event.presentation.isVisible = true
            event.presentation.isEnabled = true
            return
        }


        var enabledByExtension: String? = null
        ANY2JSON().extensionList.find { ext ->
            if (ext.isEnabled(event, actionType)) {
                enabledByExtension = ext.presentation(actionType, event)
                true
            } else {
                false
            }
        }

        if (enabledByExtension != null) {
            event.presentation.text = "Generate JSON $enabledByExtension"
            event.presentation.isVisible = true
            event.presentation.isEnabled = true
        }
        else {
            event.presentation.isVisible = false
            event.presentation.isEnabled = false
        }
    }
}

abstract class AnyToJsonBuilder<T:Any, J:Any>(
        val actionType: EType
) {

    abstract fun buildMap(type: T): J
    abstract fun presentation(): String
    abstract fun isVisible(): Boolean
}