package io.circe

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import java.io.{ BufferedWriter, ByteArrayOutputStream, OutputStreamWriter, StringWriter, Writer }
import java.nio.ByteBuffer

/**
 * Support for Jackson-powered parsing and printing for circe.
 *
 * Note that not all guarantees that hold for Jawn-based parsing and the default
 * printer will hold for the Jackson-based versions. Jackson's handling of
 * numbers in particular differs significantly: it doesn't distinguish positive
 * and negative zeros, it may truncate large JSON numbers or simply fail to
 * parse them, it may print large numbers as strings, etc.
 *
 * The implementation is ported with minimal changes from Play JSON.
 */
package object jackson extends WithJacksonMapper with JacksonParser with JacksonCompat {

  private[this] def writeJson(w: Writer, j: Json): Unit = {
    val gen = jsonGenerator(w).setPrettyPrinter(
      new DefaultPrettyPrinter()
    )

    makeWriter(mapper).writeValue(gen, j)
    w.flush()
  }

  final def jacksonPrint(json: Json): String = {
    val sw = new StringWriter
    writeJson(sw, json)
    sw.toString
  }

  private[this] class EnhancedByteArrayOutputStream extends ByteArrayOutputStream {
    def toByteBuffer: ByteBuffer = ByteBuffer.wrap(this.buf, 0, this.size)
  }

  final def jacksonPrintByteBuffer(json: Json): ByteBuffer = {
    val bytes = new EnhancedByteArrayOutputStream
    writeJson(new BufferedWriter(new OutputStreamWriter(bytes, "UTF-8")), json)
    bytes.toByteBuffer
  }
}
