package io.circe.jackson

import org.scalacheck.Prop

class ConvertingSuite extends CirceSuite with JacksonInstances {

  property("circeToJackson should correctly convert arbitrary cleaned json") {
    Prop.forAll(arbitraryCleanedJson.arbitrary) { json =>
      val node = circeToJackson(json)
      assert(parse(node.toString) === Right(json))
    }
  }

  property("jacksonToCirce should correctly convert arbitrary cleaned json") {
    Prop.forAll(arbitraryCleanedJson.arbitrary) { json =>
      val node = circeToJackson(json)
      val convertedJson = jacksonToCirce(node)
      assert(convertedJson === json)
    }
  }

}
