package org.opentorah.tei

import org.opentorah.reference.Named
import org.opentorah.xml.{From, Parser}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

final class TeiTest extends AnyFlatSpec with Matchers {

  "Parsing" should "work" in {
    val result: Tei = Parser.parseDo(From.resource(Tei, "905").parse(Tei))
  }

  "Named parsing" should "work" in {
    val result: Named = Parser.parseDo(
      From.resource(Tei, "Баал_Шем_Тов").parse(Named))

    result.role shouldBe Some("jew")
    result.names.head.name shouldBe "Израиль из Мезбича"
  }
}
