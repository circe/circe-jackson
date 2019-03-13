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
