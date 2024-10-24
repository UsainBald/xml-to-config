package com.yandexbrouser.xmltoconfigml.transformer

import com.yandexbrouser.xmltoconfigml.parser.XmlElement
import javax.script.ScriptEngineManager

object ConfigTransformer {
  val engine = ScriptEngineManager().getEngineByExtension("kts")

  private var dictionaryDepth = 0

  fun transform(root: XmlElement): String {
    val result = StringBuilder()

    root.children.forEach { child ->
      when (child.name) {
        "const" -> {
          result.append("[" + engine.eval(child.text).toString() + "]")
          if (root.name != "entry" && root.name != "item") result.append("\n")
        }
        "string" -> {
          result.append("\"" + child.text + "\"")
          if (root.name != "entry" && root.name != "item") result.append("\n")
        }
        "var" -> {
          val name = child.attributes["name"] ?: error("Variable must have a name")
          if (!name.matches(Regex("[_a-zA-Z]+"))) error("Variable name must match regex [_a-zA-Z]+")
          val value = transformValue(child.text!!)
          result.append("var $name $value;")
          if (root.name != "entry" && root.name != "item") result.append("\n")
        }
        "array" -> {
          result.append(transformArray(child))
          if (root.name != "entry" && root.name != "item") result.append("\n")
        }
        "dictionary" -> {
          result.append(transformDictionary(child))
          if (root.name != "entry" && root.name != "item") result.append("\n")
        }
        else -> error("Unknown element: ${child.name}")
      }
    }

    return result.toString()
  }

  private fun transformArray(element: XmlElement): String {
    val items = element.children.map {
      try {
        transformValue(it.text!!)
      } catch (e: Exception) {
        transform(it)
      }
    }
    return "(${items.joinToString(" ")})"
  }

  private fun transformDictionary(element: XmlElement): String {
    dictionaryDepth++
    val entries = element.children.joinToString(",\n") {
      val name = it.attributes["name"] ?: error("Dictionary entry must have a name")
      val value = try {
        transformValue(it.text!!)
      } catch(e: IllegalStateException) {
        transform(it)
      }
      "\t".repeat(dictionaryDepth) + "$name: $value"
    }
    dictionaryDepth--
    val toReturn = "{\n$entries\n" + "\t".repeat(dictionaryDepth) + "}"
    return toReturn
  }

  fun transformValue(value: String): String {
    return when {
      value.matches(Regex("\".*\"")) -> value
      value.matches(Regex("\\d+")) -> value
      value.matches(Regex("\\d+\\.\\d+")) -> value
      else -> error("Unsupported value format: $value")
    }
  }
}
