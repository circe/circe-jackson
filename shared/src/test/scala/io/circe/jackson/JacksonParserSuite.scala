package io.circe.jackson

import io.circe.Json
import io.circe.testing.ParserTests
import java.io.File
import scala.io.Source

class JacksonParserSuite extends CirceSuite with JacksonInstances {
  checkLaws("Parser", ParserTests(`package`).parser(arbitraryCleanedJson))

  "parse and decode(Accumulating)" should "fail on invalid input" in forAll { (s: String) =>
    assert(parse(s"Not JSON $s").isLeft)
    assert(decode[Json](s"Not JSON $s").isLeft)
    assert(decodeAccumulating[Json](s"Not JSON $s").isInvalid)
  }

  "parseFile and decodeFile(Accumulating)" should "parse a JSON file" in {
    val url = getClass.getResource("/io/circe/jackson/examples/glossary.json")
    val file = new File(url.toURI)

    assert(parseFile(file) === Right(glossary))
  }

  "parseByteArray and decodeByteArray(Accumulating)" should "parse an array of bytes" in {
    val stream = getClass.getResourceAsStream("/io/circe/jackson/examples/glossary.json")
    val source = Source.fromInputStream(stream)
    val bytes = source.map(_.toByte).toArray
    source.close()

    assert(parseByteArray(bytes) === Right(glossary))
  }
}
