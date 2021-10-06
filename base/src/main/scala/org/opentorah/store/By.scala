package org.opentorah.store

import org.opentorah.metadata.Names
import org.opentorah.xml.{Attribute, Parser, Unparser}

// TODO wrap a Store by a By!
trait By extends Store.NonTerminal:

  def selector: Selector

  final override def names: Names = selector.names

object By:
  private val selectorAttribute: Attribute.Required[String] = Attribute("selector").required

  val selectorParser: Parser[Selector] = selectorAttribute().map(Selector.byName)

  def selectorUnparser[T <: By]: Unparser[T] = selectorAttribute(_.selector.name)
