package com.yandexbrouser.xmltoconfigml.parser

data class XmlElement(
  val name: String,
  val attributes: Map<String, String> = emptyMap(),
  val text: String? = null,
  val children: List<XmlElement> = emptyList()
)
