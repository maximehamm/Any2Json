package io.nimbly.any2json.languages

import com.intellij.psi.xml.XmlTag
import io.nimbly.any2json.AnyToJsonBuilder
import io.nimbly.any2json.EType
import org.json.XML

class Xml2Json(actionType: EType) : AnyToJsonBuilder<XmlTag, Map<String, Any>>(actionType) {

    override fun buildMap(type: XmlTag): Map<String, Any> {
        val json = XML.toJSONObject(type.text)
        return json.toMap()
    }

    override fun presentation()
        = "from TAG"

    override fun isVisible()
        = actionType == EType.MAIN
}
