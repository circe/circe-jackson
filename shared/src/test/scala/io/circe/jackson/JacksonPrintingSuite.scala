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

import io.circe.Json
import org.scalacheck.Prop.forAll

import java.nio.ByteBuffer

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
