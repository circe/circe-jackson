package io.circe.jackson.benchmark

import cats.instances.AllInstances
import io.circe.testing.{ ArbitraryInstances, EqInstances }
import cats.syntax.AllSyntax

class ParsingBenchmarkSpec
    extends munit.FunSuite
    with AllInstances
    with AllSyntax
    with ArbitraryInstances
    with EqInstances {
  val benchmark: ParsingBenchmark = new ParsingBenchmark

  import benchmark._

  test("The parsing benchmark should correctly parse integers using Circe") {
    assert(parseIntsC === intsC)
  }

  test("correctly parse integers using Circe with Jackson") {
    assert(parseIntsCJ === intsC)
  }

  test("it should correctly parse case classes using Circe") {
    assert(parseFoosC === foosC)
  }

  test("it should correctly parse case classes using Circe with Jackson") {
    assert(parseFoosCJ === foosC)
  }
}
