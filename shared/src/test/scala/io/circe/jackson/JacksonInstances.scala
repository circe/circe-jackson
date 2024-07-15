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

package io.circe.jackson

import cats.Eq
import cats.instances.list._
import cats.instances.map._
import io.circe.Json
import io.circe.Json.JArray
import io.circe.Json.JNumber
import io.circe.Json.JObject
import io.circe.Json.JString
import io.circe.JsonBigDecimal
import io.circe.JsonBiggerDecimal
import io.circe.JsonDecimal
import io.circe.JsonDouble
import io.circe.JsonFloat
import io.circe.JsonLong
import io.circe.JsonNumber
import io.circe.numbers.BiggerDecimal
import org.scalacheck.Arbitrary

import java.nio.ByteBuffer
import scala.util.Try

import io.circe.testing.ArbitraryInstances
import scala.util.matching.Regex

trait JacksonInstances { this: ArbitraryInstances =>

  /**
   * Jackson by default in some cases serializes numbers as strings, and we want
   * to use a [[cats.Eq]] instance that takes that into account when testing.
   */
  implicit val eqJsonStringNumber: Eq[Json] = Eq.instance {
    case (JArray(a), JArray(b))   => Eq[List[Json]].eqv(a.toList, b.toList)
    case (JObject(a), JObject(b)) => Eq[Map[String, Json]].eqv(a.toMap, b.toMap)
    case (JString(a), JNumber(b)) => a == b.toString
    case (JNumber(a), JString(b)) => a.toString == b
    case (a, b)                   => Json.eqJson.eqv(a, b)
  }

  implicit val eqByteBuffer: Eq[ByteBuffer] = Eq.fromUniversalEquals

  private[this] val SigExpPattern: Regex = """[^eE]+[eE][+-]?(\d+)""".r
  private[this] val replacement: JsonNumber = JsonBiggerDecimal(BiggerDecimal.fromLong(0L), "0")

  /**
   * Jackson can't handle some very large numbers, so we replace them.
   *
   * Ideally it seems like we could set the cap at numbers with exponents larger
   * than `Int.MaxValue`, but in practice that still results in
   * `ArithmeticException`.
   */
  def cleanNumber(n: JsonNumber): JsonNumber = n.toString match {
    case SigExpPattern(exp) if !Try(exp.toLong).toOption.exists(_ <= Short.MaxValue.toLong) => replacement
    case _ =>
      n match {
        case v @ JsonDecimal(_) => cleanNumber(JsonBiggerDecimal(v.toBiggerDecimal, v.toString))
        case v @ JsonBiggerDecimal(value, _) =>
          value.toBigDecimal.map(BigDecimal(_)).fold(replacement) { d =>
            val fromBigDecimal = BiggerDecimal.fromBigDecimal(d.bigDecimal)

            if (fromBigDecimal == value && d.abs <= BigDecimal(Double.MaxValue)) v
            else JsonBiggerDecimal(fromBigDecimal, fromBigDecimal.toString)
          }
        case v @ JsonBigDecimal(_) => v
        case v @ JsonDouble(_)     => v
        case v @ JsonFloat(_)      => v
        case v @ JsonLong(_)       => v
      }
  }

  def cleanNumbers(json: Json): Json =
    json.mapNumber(cleanNumber).mapArray(_.map(cleanNumbers)).mapObject(_.mapValues(cleanNumbers))

  val arbitraryCleanedJson: Arbitrary[Json] = Arbitrary(Arbitrary.arbitrary[Json].map(cleanNumbers))
}
