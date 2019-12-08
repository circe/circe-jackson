package io.circe.jackson.benchmark

import org.scalatest.flatspec.AnyFlatSpec

class ParsingBenchmarkSpec extends AnyFlatSpec {
  val benchmark: ParsingBenchmark = new ParsingBenchmark

  import benchmark._

  "The parsing benchmark" should "correctly parse integers using Circe" in {
    assert(parseIntsC === intsC)
  }

  it should "correctly parse integers using Circe with Jackson" in {
    assert(parseIntsCJ === intsC)
  }

  it should "correctly parse case classes using Circe" in {
    assert(parseFoosC === foosC)
  }

  it should "correctly parse case classes using Circe with Jackson" in {
    assert(parseFoosCJ === foosC)
  }
}
