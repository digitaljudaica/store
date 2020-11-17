package org.opentorah.docbook

import org.opentorah.xml.{Doctype, PrettyPrinter, XLink}
import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers
import scala.xml.Elem

class PluginTest extends AnyFlatSpecLike with Matchers {

  private def test(
    name: String,
    substitutions: Map[String, String],
    doctype: Option[Doctype],
    document: Elem)(
    inIndexHtml: String*
  ): Unit = {
    val project = PluginTestProject(
      prefix = "pluginTestProjects",
      name,
      document = PrettyPrinter.default.renderXml(document, doctype),
      substitutions,
      isPdfEnabled = true
    )

    project.run()

    val indexHtml: String = project.indexHtml

    for (string: String <- inIndexHtml)
      indexHtml.contains(string) shouldBe true
  }

  "Scala XML" should "handle entity reference in dynamic attribute values - but doesn't" in {
    // Entity reference in the attribute value trips up IntelliJ's Scala XML parser:
    // it reports "No closing tag" and underlines XML literal around the entity reference in red.
    // In reality, everything works as intended:
    PrettyPrinter.default.render(<e a="http://&version;"/>) shouldBe """<e a="http://&version;"/>""" + "\n"

    // If the attribute value is enclosed in {}, making it dynamic, IntelliJ does not complain any longer -
    // but Scala XML encodes the entity reference, so nothing works:
    PrettyPrinter.default.render(<e a={"http://&version;"}/>) shouldBe """<e a="http://&amp;version;"/>""" + "\n"

    // In the tests below, I am thus forced to forego the {} - and suffer redness from IntelliJ...
  }

  "DocBook plugin" should "preserve the title" in test(
    name = "title",
    substitutions = Map.empty,
    doctype = None,
    document =
      <article xmlns={DocBook.namespace.uri} version={DocBook.version}>
        <info>
          <title>Test DocBook File</title>
        </info>
      </article>
  )(
    "Test DocBook File"
  )

  it should "resolve processing instructions and entity substitutions with DTD enabled" in test(
    name = "substitutions-with-DTD",
    substitutions = Map[String, String]("version" -> "\"v1.0.0\""),
    doctype = Some(DocBook),
    document =
      <article xmlns={DocBook.namespace.uri} version={DocBook.version} xmlns:xlink={XLink.namespace.uri}>
        <para>Processing instruction: <?eval version ?>.</para>
        <para>Processing instruction with unknown substitution: <?eval version1 ?>.</para>
        <para>Unknown processing instruction:<?eval1 XXX ?>.</para>
        <para>Entity: &version;.</para>
        <para>Entity in an attribute:<link xlink:href="http://&version;">link!</link>.</para>
      </article>
  )(
    "Processing instruction: v1.0.0.",
    "Processing instruction with unknown substitution: Evaluation failed for [version1].",
    "Unknown processing instruction:.",
    "Entity: v1.0.0.",
    """Entity in an attribute:<a class="link" href="http://v1.0.0" target="_top">link!</a>."""
  )

  it should "resolve processing instructions substitutions without DTD enabled" in test(
    name = "substitutions-without-DTD-processing-instructions",
    substitutions = Map[String, String]("version" -> "\"v1.0.0\""),
    doctype = None,
    document =
      <article xmlns={DocBook.namespace.uri} version={DocBook.version} xmlns:xlink={XLink.namespace.uri}>
        <para>Processing instruction: <?eval version ?>.</para>
        <para>Processing instruction with unknown substitution: <?eval version1 ?>.</para>
        <para>Unknown processing instruction:<?eval1 XXX ?>.</para>
      </article>
  )(
    "Processing instruction: v1.0.0.",
    "Processing instruction with unknown substitution: Evaluation failed for [version1].",
    "Unknown processing instruction:."
  )

  it should "fail resolving entity substitutions without DTD enabled" in {
    val project: PluginTestProject = PluginTestProject(
      prefix = "pluginTestProjects",
      name = "substitutions-without-DTD-entity-substitutions",
      substitutions = Map[String, String]("version" -> "\"v1.0.0\""),
      document = PrettyPrinter.default.renderXml(
        <article xmlns={DocBook.namespace.uri} version={DocBook.version} xmlns:xlink={XLink.namespace.uri}>
          <para>Processing instruction: <?eval version ?>.</para>
          <para>Entity: &version;.</para>
          <para>Entity in an attribute:<link xlink:href="http://&version;">link!</link>.</para>
        </article>
      ))

    project.fail().contains(
      """The entity "version" was referenced, but not declared.""") shouldBe true
  }
}
