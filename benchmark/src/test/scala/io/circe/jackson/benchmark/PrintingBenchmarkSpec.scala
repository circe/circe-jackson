/*
 * Copyright 2016 circe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.circe.jackson.benchmark

import cats.instances.AllInstances
import cats.syntax.AllSyntax
import io.circe.parser.decode
import io.circe.testing.ArbitraryInstances
import io.circe.testing.EqInstances

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
