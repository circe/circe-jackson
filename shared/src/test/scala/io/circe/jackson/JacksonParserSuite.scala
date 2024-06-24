/*
 * Copyright 2016 circe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.circe.jackson

import cats.data.Validated
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.{ ObjectMapper, ObjectReader }
import io.circe.Json
import io.circe.testing.ParserTests
import java.io.{ ByteArrayInputStream, File }
import org.scalacheck.Prop
import scala.io.Source
import munit.DisciplineSuite

class JacksonParserSuite extends CirceSuite with DisciplineSuite with JacksonInstances {

  checkAll("Parser", ParserTests(`package`).fromString(arbitraryCleanedJson, shrinkJson))
  checkAll(
    "Parser",
    ParserTests(`package`).fromFunction[Array[Byte]]("fromByteArray")(
      s => s.getBytes("UTF-8"),
      p => p.parseByteArray _,
      p => p.decodeByteArray[Json] _,
      p => p.decodeByteArrayAccumulating[Json] _
    )(arbitraryCleanedJson, shrinkJson)
  )

  property("parse and decode(Accumulating) should fail on invalid input") {
    Prop.forAll { (s: String) =>
      assert(parse(s"Not JSON $s").isLeft)
      assert(decode[Json](s"Not JSON $s").isLeft)
      assert(decodeAccumulating[Json](s"Not JSON $s").isInvalid)
    }
  }

  test("parseFile and decodeFile(Accumulating) should parse a JSON file") {
    val url = getClass.getResource("/io/circe/jackson/examples/glossary.json")
    val file = new File(url.toURI)

    assert(decodeFile[Json](file) === Right(glossary))
    assert(decodeFileAccumulating[Json](file) == Validated.valid(glossary))
    assert(parseFile(file) === Right(glossary))
  }

  test("parseByteArray and decodeByteArray(Accumulating) should parse an array of elementAsBytes") {
    val bytes = glossaryAsBytes

    assert(decodeByteArray[Json](bytes) === Right(glossary))
    assert(decodeByteArrayAccumulating[Json](bytes) === Validated.valid(glossary))
    assert(parseByteArray(bytes) === Right(glossary))
  }

  private val intro = "CirceJsonDeserializer should be useable with Jackson's MappingIterator "
  for (elementCount <- 1 to 4) {
    test(s"$intro with ${elementCount} elements in array") {
      val input = new ByteArrayInputStream(createJsonArrayAsBytes(glossaryAsBytes, elementCount))
      val objectMapper = new ObjectMapper()
      objectMapper.registerModule(CirceJsonModule)
      val jsonParser = objectMapper.getFactory.createParser(input)

      assert(jsonParser.nextToken() == JsonToken.START_ARRAY)
      assert(jsonParser.nextToken() == JsonToken.START_OBJECT)

      val reader = createReader(objectMapper).forType(classOf[Json])
      val iterator = reader.readValues[Json](jsonParser)
      var counter = 0
      while (iterator.hasNext) {
        val glossaryFromIterator = iterator.next()
        assert(glossary == glossaryFromIterator)
        counter = counter + 1
      }
      assert(counter == elementCount)
    }
  }

  // workaround warnings from compiler with Jackson 2.5
  @unchecked
  private def createReader(objectMapper: ObjectMapper): ObjectReader =
    objectMapper.reader()

  private def createJsonArrayAsBytes(elementAsBytes: Array[Byte], elementCount: Int): Array[Byte] = {
    val byteArrayOutput = new java.io.ByteArrayOutputStream()
    byteArrayOutput.write('[')
    for (i <- 1 to elementCount) {
      if (i != 1) {
        byteArrayOutput.write(',')
      }
      byteArrayOutput.write(elementAsBytes)
    }
    byteArrayOutput.write(']')
    byteArrayOutput.toByteArray
  }

  private def glossaryAsBytes = {
    val stream = getClass.getResourceAsStream("/io/circe/jackson/examples/glossary.json")
    val source = Source.fromInputStream(stream)
    val bytes = source.map(_.toByte).toArray
    source.close()
    bytes
  }
}
