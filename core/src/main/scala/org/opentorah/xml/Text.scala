package org.opentorah.xml

import org.opentorah.util.Effects

final class Text:
  override def toString: String = s"element text"

  private def optionalParser: Parser[Option[String]] = ParserState.takeCharacters

  def optional: Parsable[Option[String]] = new Parsable[Option[String]]:
    override protected def parser: Parser[Option[String]] = optionalParser
    override def unparser: Unparser[Option[String]] = Unparser(
      content = value => value.toSeq.map(Xml.mkText)
    )

  def required: Parsable[String] = new Parsable[String]:
    override protected def parser: Parser[String] = Effects.required(optionalParser, this)
    override def unparser: Unparser[String] = Unparser(
      content = value => Seq(Xml.mkText(value))
    )

object Text:

  def apply(): Text = new Text

