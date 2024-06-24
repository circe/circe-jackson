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

import com.fasterxml.jackson.core.{ JsonFactory, JsonParser }
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.{ File, Writer }

class WithJacksonMapper {
  final val mapper: ObjectMapper = (new ObjectMapper).registerModule(CirceJsonModule)
  private[this] final val jsonFactory: JsonFactory = new JsonFactory(mapper)

  protected final def jsonStringParser(input: String): JsonParser = jsonFactory.createParser(input)
  protected final def jsonFileParser(file: File): JsonParser = jsonFactory.createParser(file)
  protected final def jsonBytesParser(bytes: Array[Byte]): JsonParser =
    jsonFactory.createParser(bytes)
  protected final def jsonGenerator(out: Writer) = jsonFactory.createGenerator(out)
}
