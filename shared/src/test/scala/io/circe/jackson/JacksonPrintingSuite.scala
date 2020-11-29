package io.circe.jackson

import io.circe.Json
import java.nio.ByteBuffer
import org.scalacheck.Prop.forAll

class JacksonPrintingSuite extends CirceSuite with JacksonInstances {
  property("jacksonPrint should produce round-trippable output") {
    forAll { (json: Json) =>
      io.circe.jawn.parse(jacksonPrint(json)) === Right(json)
    }
  }

  property("jacksonPrintByteBuffer should produce the same output as jacksonPrint") {
    forAll { (json: Json) =>
      jacksonPrintByteBuffer(json) === ByteBuffer.wrap(jacksonPrint(json).getBytes("UTF-8"))
    }
  }
}
