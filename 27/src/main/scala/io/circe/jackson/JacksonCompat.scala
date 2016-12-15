package io.circe.jackson

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.{ DeserializationContext, ObjectMapper, ObjectWriter }

private[jackson] trait JacksonCompat {
  protected def makeWriter(mapper: ObjectMapper): ObjectWriter = mapper.writerWithDefaultPrettyPrinter()

  protected def handleUnexpectedToken(context: DeserializationContext)(
    klass: Class[_],
    parser: JsonParser
  ): Unit =
    throw context.mappingException(klass)
}
