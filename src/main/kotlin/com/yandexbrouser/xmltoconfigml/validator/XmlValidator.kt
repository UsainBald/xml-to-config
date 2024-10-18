package com.yandexbrouser.xmltoconfigml.validator

import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

object XmlValidator {
  fun validate(file: File) {
    try {
      val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)
      document.documentElement.normalize()
    } catch (e: Exception) {
      throw IllegalArgumentException("Invalid XML format: ${e.message}")
    }
  }
}
