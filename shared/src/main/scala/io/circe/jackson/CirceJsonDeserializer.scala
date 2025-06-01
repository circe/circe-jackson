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
import com.fasterxml.jackson.core.JsonTokenId
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.`type`.TypeFactory
import io.circe.Json
import io.circe.JsonBigDecimal

import java.util.ArrayList
import scala.annotation.nowarn
import scala.annotation.switch
import scala.annotation.tailrec
import scala.jdk.CollectionConverters._

private[jackson] sealed trait DeserializerContext {
  def addValue(value: Json): DeserializerContext
}

private[jackson] final case class ReadingList(content: ArrayList[Json]) extends DeserializerContext {
  final def addValue(value: Json): DeserializerContext = ReadingList { content.add(value); content }
}

private[jackson] final case class KeyRead(content: ArrayList[(String, Json)], fieldName: String)
    extends DeserializerContext {
  def addValue(value: Json): DeserializerContext = ReadingMap {
    content.add(fieldName -> value)
    content
  }
}

private[jackson] final case class ReadingMap(content: ArrayList[(String, Json)]) extends DeserializerContext {
  final def setField(fieldName: String): DeserializerContext = KeyRead(content, fieldName)
  final def addValue(value: Json): DeserializerContext =
    throw new Exception("Cannot add a value on an object without a key, malformed JSON object!")
}

private[jackson] final class CirceJsonDeserializer(@nowarn factory: TypeFactory, klass: Class[_])
    extends JsonDeserializer[Object]
    with JacksonCompat {
  override final def isCachable: Boolean = true

  override final def deserialize(jp: JsonParser, ctxt: DeserializationContext): Json = {
    val value = deserialize(jp, ctxt, List())
    if (!klass.isAssignableFrom(value.getClass)) handleUnexpectedToken(ctxt)(klass, jp)

    value
  }

  @tailrec
  final def deserialize(
    jp: JsonParser,
    ctxt: DeserializationContext,
    parserContext: List[DeserializerContext]
  ): Json = {
    if (jp.getCurrentToken == null) {
      jp.nextToken()
      ()
    }

    val (maybeValue, nextContext) = (jp.getCurrentToken.id(): @switch) match {
      case JsonTokenId.ID_NUMBER_INT | JsonTokenId.ID_NUMBER_FLOAT =>
        (Some(Json.JNumber(JsonBigDecimal(jp.getDecimalValue))), parserContext)

      case JsonTokenId.ID_STRING      => (Some(Json.JString(jp.getText)), parserContext)
      case JsonTokenId.ID_TRUE        => (Some(Json.JBoolean(true)), parserContext)
      case JsonTokenId.ID_FALSE       => (Some(Json.JBoolean(false)), parserContext)
      case JsonTokenId.ID_NULL        => (Some(Json.JNull), parserContext)
      case JsonTokenId.ID_START_ARRAY => (None, ReadingList(new ArrayList) +: parserContext)

      case JsonTokenId.ID_END_ARRAY =>
        parserContext match {
          case ReadingList(content) :: stack =>
            (Some(Json.fromValues(content.asScala)), stack)
          case _ => throw new RuntimeException("We weren't reading a list, something went wrong")
        }

      case JsonTokenId.ID_START_OBJECT => (None, ReadingMap(new ArrayList) +: parserContext)

      case JsonTokenId.ID_FIELD_NAME =>
        parserContext match {
          case (c: ReadingMap) :: stack => (None, c.setField(currentName(jp)) +: stack)
          case _ => throw new RuntimeException("We weren't reading an object, something went wrong")
        }

      case JsonTokenId.ID_END_OBJECT =>
        parserContext match {
          case ReadingMap(content) :: stack =>
            (
              Some(Json.fromFields(content.asScala)),
              stack
            )
          case _ => throw new RuntimeException("We weren't reading an object, something went wrong")
        }

      case JsonTokenId.ID_NOT_AVAILABLE =>
        throw new RuntimeException("We weren't reading an object, something went wrong")

      case JsonTokenId.ID_EMBEDDED_OBJECT =>
        throw new RuntimeException("We weren't reading an object, something went wrong")
    }

    maybeValue match {
      case Some(v) if nextContext.isEmpty => v
      case maybeValue                     =>
        jp.nextToken()
        val toPass = maybeValue.map { v =>
          nextContext match {
            case previous :: stack =>
              (previous.addValue(v)) +: stack
            case Nil => nextContext
          }
        }.getOrElse(nextContext)

        deserialize(jp, ctxt, toPass)
    }
  }

  override final def getNullValue = Json.JNull
}
