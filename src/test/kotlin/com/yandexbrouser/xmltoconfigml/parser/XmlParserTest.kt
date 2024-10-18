package com.yandexbrouser.xmltoconfigml.parser

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class XmlParserTest {

  @Test
  fun `should parse simple XML`() {
    val file = File.createTempFile("simple", ".xml")
    file.writeText("""
            <root>
                <var name="username">"Alice"</var>
            </root>
        """.trimIndent())

    try {
      val result = XmlParser.parse(file)

      assertEquals("root", result.name)
      assertEquals(1, result.children.size)
      assertEquals("var", result.children[0].name)
      assertEquals("username", result.children[0].attributes["name"])
      assertEquals("\"Alice\"", result.children[0].text)
    } finally {
      file.delete()
    }
  }

  @Test
  fun `should parse XML with nested elements`() {
    val file = File.createTempFile("nested", ".xml")
    file.writeText("""
            <root>
                <dictionary>
                    <entry name="city">"New York"</entry>
                    <entry name="zipcode">10001</entry>
                </dictionary>
            </root>
        """.trimIndent())

    try {
      val result = XmlParser.parse(file)

      assertEquals("root", result.name)
      assertEquals(1, result.children.size)
      assertEquals("dictionary", result.children[0].name)

      val dictionary = result.children[0]
      assertEquals(2, dictionary.children.size)

      val cityEntry = dictionary.children[0]
      assertEquals("entry", cityEntry.name)
      assertEquals("city", cityEntry.attributes["name"])
      assertEquals("\"New York\"", cityEntry.text)

      val zipcodeEntry = dictionary.children[1]
      assertEquals("entry", zipcodeEntry.name)
      assertEquals("zipcode", zipcodeEntry.attributes["name"])
      assertEquals("10001", zipcodeEntry.text)
    } finally {
      file.delete()
    }
  }

  @Test
  fun `should handle empty XML document`() {
    val file = File.createTempFile("empty", ".xml")
    file.writeText("<root></root>")

    try {
      val result = XmlParser.parse(file)

      assertEquals("root", result.name)
      assertTrue(result.children.isEmpty())
    } finally {
      file.delete()
    }
  }
}
