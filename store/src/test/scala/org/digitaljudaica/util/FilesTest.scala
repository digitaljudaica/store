package org.digitaljudaica.util

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

final class FilesTest extends AnyFlatSpec with Matchers {

  "nameAndExtension" should "work" in {
    Files.nameAndExtension("x") shouldBe ("x", None)
    Files.nameAndExtension("x.xml") shouldBe ("x", Some("xml"))
  }
}
