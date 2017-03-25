package io.circe.jackson


import cats.data.Validated
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.{ObjectMapper, ObjectReader}
import io.circe.Json
import io.circe.testing.ParserTests
import java.io.{ByteArrayInputStream, File}

import scala.io.Source

class JacksonParserSuite extends CirceSuite with JacksonInstances {
  checkLaws("Parser", ParserTests(`package`).fromString(arbitraryCleanedJson))
  checkLaws("Parser", ParserTests(`package`)
    .fromFunction[Array[Byte]]("fromByteArray")(
      s => s.getBytes("UTF-8"),
      p => p.parseByteArray _,
      p => p.decodeByteArray[Json] _,
      p => p.decodeByteArrayAccumulating[Json] _
    )(arbitraryCleanedJson)
  )

  "parse and decode(Accumulating)" should "fail on invalid input" in forAll { (s: String) =>
    assert(parse(s"Not JSON $s").isLeft)
    assert(decode[Json](s"Not JSON $s").isLeft)
    assert(decodeAccumulating[Json](s"Not JSON $s").isInvalid)
  }

  "parseFile and decodeFile(Accumulating)" should "parse a JSON file" in {
    val url = getClass.getResource("/io/circe/jackson/examples/glossary.json")
    val file = new File(url.toURI)

    assert(decodeFile[Json](file) === Right(glossary))
    assert(decodeFileAccumulating[Json](file) == Validated.valid(glossary))
    assert(parseFile(file) === Right(glossary))
  }

  "parseByteArray and decodeByteArray(Accumulating)" should "parse an array of elementAsBytes" in {
    val bytes = glossaryAsBytes

    assert(decodeByteArray[Json](bytes) === Right(glossary))
    assert(decodeByteArrayAccumulating[Json](bytes) === Validated.valid(glossary))
    assert(parseByteArray(bytes) === Right(glossary))
  }

  for (elementCount <- 1 to 4) {
    "CirceJsonDeserializer" should s"be useable with Jackson's MappingIterator " +
      s"with ${elementCount} elements in array" in {
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
