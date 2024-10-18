package com.yandexbrouser.xmltoconfigml

import com.yandexbrouser.xmltoconfigml.parser.XmlParser
import com.yandexbrouser.xmltoconfigml.transformer.ConfigTransformer
import com.yandexbrouser.xmltoconfigml.validator.XmlValidator
import java.io.File

fun main(args: Array<String>) {
  if (args.isEmpty()) {
    println("Usage: ./gradlew xmltoconfigml <path-to-xml-file>")
    return
  }

  val filePath = args[0]
  val xmlFile = File(filePath)

  if (!xmlFile.exists()) {
    println("File not found: $filePath")
    return
  }

  try {
    XmlValidator.validate(xmlFile)

    val rootElement = XmlParser.parse(xmlFile)

    val configOutput = ConfigTransformer.transform(rootElement)

    println(configOutput)
  } catch (e: Exception) {
    println("Error: ${e.message}")
  }
}
