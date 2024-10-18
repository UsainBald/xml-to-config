package com.yandexbrouser.xmltoconfigml.parser

import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

object XmlParser {
  fun parse(file: File): XmlElement {
    val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)
    document.documentElement.normalize()
    return parseElement(document.documentElement)
  }

  fun parseElement(element: Element): XmlElement {
    val children = mutableListOf<XmlElement>()
    val childNodes = element.childNodes

    for (i in 0..< childNodes.length) {
      val node = childNodes.item(i)
      if (node.nodeType == Node.ELEMENT_NODE) {
        children.add(parseElement(node as Element))
      }
    }

    return XmlElement(
      name = element.tagName,
      attributes = element.attributes.let {
        (0..< it.length).associate { idx ->
          val attr = it.item(idx)
          attr.nodeName to attr.nodeValue
        }
      },
      text = element.textContent.trim().takeIf { it.isNotEmpty() },
      children = children
    )
  }
}
