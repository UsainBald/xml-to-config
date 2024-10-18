package com.yandexbrouser.xmltoconfigml.transformer

import com.yandexbrouser.xmltoconfigml.parser.XmlElement

object ConfigTransformer {
  fun transform(root: XmlElement): String {
    val result = StringBuilder()

    root.children.forEach { child ->
      when (child.name) {
        "var" -> {
          val name = child.attributes["name"] ?: error("Variable must have a name")
          val value = transformValue(child.text!!)
          result.append("var $name $value;\n")
        }
        "array" -> {
          result.append(transformArray(child))
        }
        "dictionary" -> {
          result.append(transformDictionary(child))
        }
        else -> error("Unknown element: ${child.name}")
      }
    }

    return result.toString()
  }

  private fun transformArray(element: XmlElement): String {
    val items = element.children.map { transformValue(it.text!!) }
    return "(${items.joinToString(" ")})\n"
  }

  private fun transformDictionary(element: XmlElement): String {
    val entries = element.children.joinToString(",\n") {
      val name = it.attributes["name"] ?: error("Dictionary entry must have a name")
      val value =  try {
        transformValue(it.text!!)
      } catch(e: IllegalStateException) {
        transform(it)
      }
      "\t$name: $value"
    }
    return "{\n$entries}\n"
  }

  fun transformValue(value: String): String {
    return when {
      value.matches(Regex("\".*\"")) -> value // It's a string, return as is
      value.matches(Regex("\\d+")) -> value   // It's an integer, return as is
      value.matches(Regex("\\d+\\.\\d+")) -> value // It's a float, return as is
      else -> error("Unsupported value format: $value")
    }
  }
}
