package com.yandexbrouser.xmltoconfigml.validator

import java.io.File
import kotlin.test.Test
import kotlin.test.assertFailsWith

class XmlValidatorTest {

  @Test
  fun `should validate valid XML`() {
    val file = File.createTempFile("valid", ".xml")
    file.writeText("""
            <root>
                <var name="username">"Alice"</var>
            </root>
        """.trimIndent())

    try {
      XmlValidator.validate(file)
    } finally {
      file.delete()
    }
  }

  @Test
  fun `should throw error for invalid XML`() {
    val file = File.createTempFile("invalid", ".xml")
    file.writeText("<root><var name=\"username\">Alice</var>") // Malformed XML

    try {
      assertFailsWith<IllegalArgumentException> {
        XmlValidator.validate(file)
      }
    } finally {
      file.delete()
    }
  }
}
