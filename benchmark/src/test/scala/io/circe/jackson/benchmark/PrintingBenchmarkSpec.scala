package io.circe.jackson.benchmark

import io.circe.parser.decode
import java.nio.ByteBuffer
import org.scalatest.flatspec.AnyFlatSpec

class PrintingBenchmarkSpec extends AnyFlatSpec {
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

  it should "correctly print integers using Circe with Jackson" in {
    assert(decodeInts(printIntsCJString) === Some(ints))
    assert(decodeInts(byteBufferToString(printIntsCJBytes)) === Some(ints))
  }

  it should "correctly print case classes using Circe with Jackson" in {
    assert(decodeFoos(printFoosCJString) === Some(foos))
    assert(decodeFoos(byteBufferToString(printFoosCJBytes)) === Some(foos))
  }
}
