package io.circe.jackson

import cats.instances.AllInstances
import cats.syntax.{ AllSyntax, EitherOps }
import io.circe.Json
import io.circe.testing.{ ArbitraryInstances, EqInstances }
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import org.typelevel.discipline.scalatest.FlatSpecDiscipline
import scala.language.implicitConversions

/**
 * An opinionated stack of traits to improve consistency and reduce boilerplate in circe tests.
 */
trait CirceSuite
    extends AnyFlatSpec
    with FlatSpecDiscipline
    with ScalaCheckDrivenPropertyChecks
    with AllInstances
    with AllSyntax
    with ArbitraryInstances
    with EqInstances {

  override def convertToEqualizer[T](left: T): Equalizer[T] =
    sys.error("Intentionally ambiguous implicit for Equalizer")

  implicit def prioritizedCatsSyntaxEither[A, B](eab: Either[A, B]): EitherOps[A, B] = new EitherOps(eab)

  val glossary: Json = Json.obj(
    "glossary" -> Json.obj(
      "title" -> Json.fromString("example glossary"),
      "GlossDiv" -> Json.obj(
        "title" -> Json.fromString("S"),
        "GlossList" -> Json.obj(
          "GlossEntry" -> Json.obj(
            "ID" -> Json.fromString("SGML"),
            "SortAs" -> Json.fromString("SGML"),
            "GlossTerm" -> Json.fromString("Standard Generalized Markup Language"),
            "Acronym" -> Json.fromString("SGML"),
            "Abbrev" -> Json.fromString("ISO 8879:1986"),
            "GlossDef" -> Json.obj(
              "para" -> Json.fromString(
                "A meta-markup language, used to create markup languages such as DocBook."
              ),
              "GlossSeeAlso" -> Json.arr(Json.fromString("GML"), Json.fromString("XML"))
            ),
            "GlossSee" -> Json.fromString("markup")
          )
        )
      )
    )
  )
}
