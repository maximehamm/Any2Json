package io.nimbly.any2json.languages

import com.intellij.psi.xml.XmlTag
import io.nimbly.any2json.AnyToJsonBuilder
import org.json.XML

class Xml2Json() : AnyToJsonBuilder<XmlTag>() {

    override fun buildMap(type: XmlTag, generateValues: Boolean): Map<String, Any> {
        val json = XML.toJSONObject(type.text)
        return json.toMap()
    }

    override fun presentation(): String = "from TAG"

    override fun isVisible(generateValues: Boolean) = !generateValues
}
