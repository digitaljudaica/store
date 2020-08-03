package org.opentorah.tei

import org.opentorah.xml.{Antiparser, Attribute, ContentType, Element, Parsable, Parser, ToXml, Xml}
import scala.xml.Node

final case class Entity private(
  id: Option[String],
  entityType: EntityType,
  role: Option[String],
  names: Seq[EntityName],
  content: Seq[Node]
) {
  def name: String = names.head.name
}

object Entity extends Parsable[Entity] with ToXml[Entity] {

  private val roleAttribute: Attribute[String] = Attribute("role")

  override def toString: String = "Entity"

  override val name2parser: Map[String, Parsable.ContentTypeAndParser[Entity]] = EntityType.values.map { entity =>
    entity.element -> new Parsable.ContentTypeAndParser[Entity](ContentType.Elements, parser(entity))
  }.toMap

  private def parser(entityType: EntityType): Parser[Entity] = for {
    id <- Attribute.id.optional
    role <- roleAttribute.optional
    names <- EntityName.parsable(entityType).all
    _ <- Parser.check(names.nonEmpty, s"No names in $id")
    content <- Element.allNodes
  } yield new Entity(
    id,
    entityType,
    role,
    names,
    content = content.map(Xml.removeNamespace),
  )

  override protected def elementName(value: Entity): String = value.entityType.element

  override protected val antiparser: Antiparser[Entity] = Antiparser(
    Attribute.id.toAntiparserOption.premap(_.id),
    roleAttribute.toAntiparserOption.premap(_.role),
    EntityName.elementAntiparserSeq.premap(_.names),
    Antiparser.xml.premap[Entity](_.content)
  )
}
