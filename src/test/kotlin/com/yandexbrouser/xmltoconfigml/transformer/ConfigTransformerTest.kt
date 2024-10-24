package com.yandexbrouser.xmltoconfigml.transformer

import com.yandexbrouser.xmltoconfigml.parser.XmlElement
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ConfigTransformerTest {

  @Test
  fun `should transform with simple var`() {
    val xmlElement = XmlElement(
      name = "root",
      children = listOf(
        XmlElement(name = "var", attributes = mapOf("name" to "username"), text = "\"Alice\"")
      )
    )
    val result = ConfigTransformer.transform(xmlElement)
    assertEquals("var username \"Alice\";\n", result)
  }

  @Test
  fun `should transform with array`() {
    val xmlElement = XmlElement(
      name = "root",
      children = listOf(
        XmlElement(
          name = "array",
          children = listOf(
            XmlElement(name = "item", text = "1"),
            XmlElement(name = "item", text = "2"),
            XmlElement(name = "item", text = "3")
          )
        )
      )
    )
    val result = ConfigTransformer.transform(xmlElement)
    assertEquals("(1 2 3)\n", result)
  }

  @Test
  fun `should transform with dictionary`() {
    val xmlElement = XmlElement(
      name = "root",
      children = listOf(
        XmlElement(
          name = "dictionary",
          children = listOf(
            XmlElement(name = "entry", attributes = mapOf("name" to "city"), text = "\"New York\""),
            XmlElement(name = "entry", attributes = mapOf("name" to "zipcode"), text = "10001")
          )
        )
      )
    )
    val result = ConfigTransformer.transform(xmlElement)
    assertEquals("{\n\tcity: \"New York\",\n\tzipcode: 10001\n}\n", result)
  }

  @Test
  fun `should throw error for unsupported element`() {
    val xmlElement = XmlElement(
      name = "root",
      children = listOf(
        XmlElement(name = "unsupported", text = "value")
      )
    )
    assertFailsWith<IllegalStateException> {
      ConfigTransformer.transform(xmlElement)
    }
  }

  @Test
  fun `should throw error for invalid value format`() {
    val xmlElement = XmlElement(name = "item", text = "invalid")
    assertFailsWith<IllegalStateException> {
      ConfigTransformer.transformValue(xmlElement.text!!)
    }
  }
}
