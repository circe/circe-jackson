package io.circe.jackson.benchmark

import io.circe.parser.decode
import org.scalatest.FlatSpec

class PrintingBenchmarkSpec extends FlatSpec {
  val benchmark: PrintingBenchmark = new PrintingBenchmark

  import benchmark._

  private[this] def decodeInts(json: String): Option[List[Int]] =
    decode[List[Int]](json).toOption

  private[this] def decodeFoos(json: String): Option[Map[String, Foo]] =
    decode[Map[String, Foo]](json).toOption

  it should "correctly print integers using Circe with Jackson" in {
    assert(decodeInts(printIntsCJ) === Some(ints))
  }

  it should "correctly print case classes using Circe with Jackson" in {
    assert(decodeFoos(printFoosCJ) === Some(foos))
  }
}
