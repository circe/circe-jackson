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
import io.circe.testing.ArbitraryInstances
import io.circe.testing.EqInstances

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
