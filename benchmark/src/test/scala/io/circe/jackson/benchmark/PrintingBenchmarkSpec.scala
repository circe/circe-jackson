package io.circe.jackson.benchmark

import cats.instances.AllInstances
import io.circe.testing.{ ArbitraryInstances, EqInstances }
import cats.syntax.{ AllSyntax }
import io.circe.parser.decode
import java.nio.ByteBuffer

class PrintingBenchmarkSpec
    extends munit.FunSuite
    with AllInstances
    with AllSyntax
    with ArbitraryInstances
    with EqInstances {
  val benchmark: PrintingBenchmark = new PrintingBenchmark

  import benchmark._

  private[this] def byteBufferToString(bb: ByteBuffer): String = {
    val array = new Array[Byte](bb.remaining)
    bb.get(array)
    new String(array, "UTF-8")
  }

  private[this] def decodeInts(json: String): Option[List[Int]] =
    decode[List[Int]](json).fold(_ => None, Some(_))

  private[this] def decodeFoos(json: String): Option[Map[String, Foo]] =
    decode[Map[String, Foo]](json).fold(_ => None, Some(_))

  test("correctly print integers using Circe with Jackson") {
    assert(decodeInts(printIntsCJString) === Some(ints))
    assert(decodeInts(byteBufferToString(printIntsCJBytes)) === Some(ints))
  }

  test("correctly print case classes using Circe with Jackson") {
    assert(decodeFoos(printFoosCJString) === Some(foos))
    assert(decodeFoos(byteBufferToString(printFoosCJBytes)) === Some(foos))
  }
}
