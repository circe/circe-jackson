package io.circe.jackson.benchmark

import cats.Eq
import io.circe.{ Decoder, Encoder, Json => JsonC }
import io.circe.generic.semiauto._
import io.circe.jawn._
import java.util.concurrent.TimeUnit
import org.openjdk.jmh.annotations._

case class Foo(s: String, d: Double, i: Int, l: Long, bs: List[Boolean])

object Foo {
  implicit val decodeFoo: Decoder[Foo] = deriveDecoder
  implicit val encodeFoo: Encoder[Foo] = deriveEncoder
  implicit val eqFoo: Eq[Foo] = Eq.fromUniversalEquals[Foo]
}

class ExampleData {
  val ints: List[Int] = (0 to 1000).toList

  val foos: Map[String, Foo] = List.tabulate(100) { i =>
    ("b" * i) -> Foo("a" * i, (i + 2.0) / (i + 1.0), i, i * 1000L, (0 to i).map(_ % 2 == 0).toList)
  }.toMap

  @inline def encodeC[A](a: A)(implicit encode: Encoder[A]): JsonC = encode(a)

  val intsC: JsonC = encodeC(ints)
  val foosC: JsonC = encodeC(foos)

  val intsJson: String = intsC.noSpaces
  val foosJson: String = foosC.noSpaces
}

/**
 * Compare the performance of parsing operations.
 *
 * The following command will run the benchmarks with reasonable settings:
 *
 * > sbt "benchmark/jmh:run -i 10 -wi 10 -f 2 -t 1 io.circe.jackson.benchmark.ParsingBenchmark"
 */
@State(Scope.Thread)
@BenchmarkMode(Array(Mode.Throughput))
@OutputTimeUnit(TimeUnit.SECONDS)
class ParsingBenchmark extends ExampleData {
  @Benchmark
  def parseIntsC: JsonC = parse(intsJson).right.getOrElse(throw new Exception)

  @Benchmark
  def parseIntsCJ: JsonC = io.circe.jackson.parse(intsJson).right.getOrElse(throw new Exception)

  @Benchmark
  def parseFoosC: JsonC = parse(foosJson).right.getOrElse(throw new Exception)

  @Benchmark
  def parseFoosCJ: JsonC = io.circe.jackson.parse(foosJson).right.getOrElse(throw new Exception)
}

/**
 * Compare the performance of printing operations.
 *
 * The following command will run the benchmarks with reasonable settings:
 *
 * > sbt "benchmark/jmh:run -i 10 -wi 10 -f 2 -t 1 io.circe.jackson.benchmark.PrintingBenchmark"
 */
@State(Scope.Thread)
@BenchmarkMode(Array(Mode.Throughput))
@OutputTimeUnit(TimeUnit.SECONDS)
class PrintingBenchmark extends ExampleData {
  @Benchmark
  def printIntsC: String = intsC.noSpaces

  @Benchmark
  def printIntsCJ: String = io.circe.jackson.jacksonPrint(intsC)

  @Benchmark
  def printFoosC: String = foosC.noSpaces

  @Benchmark
  def printFoosCJ: String = io.circe.jackson.jacksonPrint(foosC)
}
