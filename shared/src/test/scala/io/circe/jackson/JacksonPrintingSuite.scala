package io.circe.jackson

import io.circe.Json
import java.nio.ByteBuffer

class JacksonPrintingSuite extends CirceSuite with JacksonInstances {
  "jacksonPrint" should "produce round-trippable output" in forAll(arbitraryCleanedJson.arbitrary) { (json: Json) =>
    assert(io.circe.jawn.parse(jacksonPrint(json)) === Right(json))
  }

  "jacksonPrintByteBuffer" should "produce the same output as jacksonPrint" in forAll(arbitraryCleanedJson.arbitrary) {
    (json: Json) => assert(jacksonPrintByteBuffer(json) === ByteBuffer.wrap(jacksonPrint(json).getBytes("UTF-8")))
  }
}
