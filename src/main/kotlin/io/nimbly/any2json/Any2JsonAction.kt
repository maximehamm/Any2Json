package io.nimbly.any2json

import com.google.gson.GsonBuilder
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import io.nimbly.any2json.languages.Java2Json
import io.nimbly.any2json.languages.Kotlin2Json
import io.nimbly.any2json.util.Any2PojoException
import io.nimbly.any2json.util.error
import io.nimbly.any2json.util.info
import io.nimbly.any2json.util.warn
import org.jetbrains.kotlin.psi.KtClass
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

class Any2JsonDefaultAction : Any2JsonAction(false)

class Any2JsonRandomAction : Any2JsonAction(true)

abstract class Any2JsonAction(val generateValues: Boolean): AnAction() {

    override fun actionPerformed(e: AnActionEvent) {

        val project = e.project!!
        val editor = e.getData(CommonDataKeys.EDITOR)
        val psiFile = e.getData(CommonDataKeys.PSI_FILE)
        val element = psiFile!!.findElementAt(editor!!.caretModel.offset)

        try {

            // Build map
            val (className, map) =
                   buildFromJava(element)
                ?: buildFromKotlin(element)
                ?: throw Any2PojoException("Not supported target !")

            // Convert to Json
            val json = GsonBuilder().setPrettyPrinting().create().toJson(map)

            // Put to clipboard
            Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(json), StringSelection(json))

            // Report notificagton
            info("${className} to JSON copied to clipboard !", project)

        } catch (ex: Any2PojoException) {
            warn(ex.message!!, project)
        } catch (ex: Exception) {
            ex.printStackTrace()
            error("Any to JSON error !", project)
        }
    }

    private fun buildFromJava(element: PsiElement?): Pair<String, Map<String, Any>>? {
        val psiClass = PsiTreeUtil.getContextOfType(element, PsiClass::class.java)
            ?: return null
        return Pair(psiClass.name!!,
            Java2Json().buildMap(psiClass, generateValues))
    }

    private fun buildFromKotlin(element: PsiElement?): Pair<String, Map<String, Any>>? {
        val ktClass = PsiTreeUtil.getContextOfType(element, KtClass::class.java)
            ?: return null
        return Pair(ktClass.name!!,
            Kotlin2Json().buildMap(ktClass, generateValues))
    }
}

abstract class AnyToJsonBuilder<T : PsiElement> {
    abstract fun buildMap(type: T, generateValues: Boolean): Map<String, Any>
}