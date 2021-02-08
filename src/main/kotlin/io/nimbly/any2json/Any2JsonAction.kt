package io.nimbly.any2json

import com.google.gson.GsonBuilder
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.xdebugger.frame.XValueNode
import com.intellij.xdebugger.impl.ui.tree.XDebuggerTree
import com.intellij.xdebugger.impl.ui.tree.nodes.XStackFrameNode
import com.intellij.xdebugger.impl.ui.tree.nodes.XValueContainerNode
import com.intellij.xdebugger.impl.ui.tree.nodes.XValueNodeImpl
import io.nimbly.any2json.debugger.Variable2Json
import io.nimbly.any2json.languages.Java2Json
import io.nimbly.any2json.languages.Kotlin2Json
import io.nimbly.any2json.util.Any2PojoException
import io.nimbly.any2json.util.error
import io.nimbly.any2json.util.info
import io.nimbly.any2json.util.warn
import org.jetbrains.kotlin.psi.KtClass
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import javax.swing.tree.TreeNode

class Any2JsonDefaultAction : Any2JsonAction(false) {
    override fun presentationSuffix() = ""
}

class Any2JsonRandomAction : Any2JsonAction(true) {
    override fun presentationSuffix() = " with Data"
}

abstract class Any2JsonAction(private val generateValues: Boolean): AnAction() {

    override fun actionPerformed(e: AnActionEvent) {

        val project = e.project!!

        try {

            val editor = e.getData(CommonDataKeys.EDITOR)
            val psiFile = e.getData(CommonDataKeys.PSI_FILE)

            var result: Pair<String, Map<String, Any>>? = null
            if (psiFile != null && editor!=null) {
                val element = psiFile.findElementAt(editor.caretModel.offset)
                result = buildFromJava(element)
                      ?: buildFromKotlin(element)
                      ?: throw Any2PojoException("Not supported target !")
            }
            else {
                result = buildDebugger(e)
            }

            if (result == null)
                throw Any2PojoException("Unable to define context !")

            // Convert to Json
            val json = GsonBuilder().setPrettyPrinting().create().toJson(result.second)

            // Put to clipboard
            Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(json), StringSelection(json))

            // Report notificagton
            info("${result.first} to JSON copied to clipboard !", project)

        } catch (ex: Any2PojoException) {
            warn(ex.message!!, project)
        } catch (ex: Exception) {
            ex.printStackTrace()
            error("Any to JSON error !", project)
        }
    }

    private fun buildDebugger(e: AnActionEvent): Pair<String, Map<String, Any>>? {
        val xtree: XDebuggerTree = e.dataContext.getData("xdebugger.tree") as XDebuggerTree
        val xroot: XStackFrameNode = xtree.root as XStackFrameNode
        val xnode: XValueNodeImpl = xroot.children.find { it is XValueNodeImpl && it.name != "this" } as XValueNodeImpl?
            ?: return null

        return Pair(xnode.name!!,
            Variable2Json().buildMap(xnode, generateValues))
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

    override fun update(e: AnActionEvent) {

        var visible = false;
        var text = "Generate JSON"
        val editor = e.getData(CommonDataKeys.EDITOR)
        val psiFile = e.getData(CommonDataKeys.PSI_FILE)
        if (psiFile != null) {
            if (editor != null) {
                val element = psiFile.findElementAt(editor.caretModel.offset)
                if (PsiTreeUtil.getContextOfType(element, PsiClass::class.java) !=null) {
                    visible = true;
                    text = "Generate JSON " + Java2Json().presentation() + presentationSuffix()
                }
                else if (PsiTreeUtil.getContextOfType(element, KtClass::class.java) !=null) {
                    visible = true;
                    text = "Generate JSON " + Kotlin2Json().presentation() + presentationSuffix()
                }
            }
        }
        else {
            visible = true
            text = "Generate JSON " + Variable2Json().presentation() + presentationSuffix()
        }

        e.presentation.text = text
        e.presentation.isVisible = visible
        e.presentation.isEnabled = true
    }

    abstract fun presentationSuffix(): String
}

abstract class AnyToJsonBuilder<T : Any> {
    abstract fun buildMap(type: T, generateValues: Boolean): Map<String, Any>
    abstract fun presentation(): String
}