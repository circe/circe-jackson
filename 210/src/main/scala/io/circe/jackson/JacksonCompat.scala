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

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectWriter
import com.fasterxml.jackson.databind.node.ObjectNode

private[jackson] trait JacksonCompat {
  protected def makeWriter(mapper: ObjectMapper): ObjectWriter = mapper.writerWithDefaultPrettyPrinter()

  protected def handleUnexpectedToken(context: DeserializationContext)(
    klass: Class[_],
    parser: JsonParser
  ): Unit = {
    context.handleUnexpectedToken(klass, parser)
    ()
  }

  protected def objectNodeSetAll(node: ObjectNode, fields: java.util.Map[String, JsonNode]): JsonNode =
    node.setAll[JsonNode](fields)

  protected def currentName(jp: JsonParser): String = jp.currentName
}
