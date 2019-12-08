package io.circe.jackson

import org.scalatest.matchers.should.Matchers

class ConvertingSuite extends CirceSuite with Matchers with JacksonInstances {

  "circeToJackson" should "correctly convert arbitrary cleaned json" in {
    forAll(arbitraryCleanedJson.arbitrary) { json =>
      val node = circeToJackson(json)
      parse(node.toString) shouldEqual Right(json)
    }
  }

  "jacksonToCirce" should "correctly convert arbitrary cleaned json" in {
    forAll(arbitraryCleanedJson.arbitrary) { json =>
      val node = circeToJackson(json)
      val convertedJson = jacksonToCirce(node)

      convertedJson shouldEqual json
    }
  }

}
