package org.opentorah.collector

import org.opentorah.metadata.Names
import org.opentorah.html
import org.opentorah.tei.{EntityReference, Unclear}
import org.opentorah.site.HtmlContent
import org.opentorah.store.{Caching, Store, WithSource}
import org.opentorah.util.Strings
import org.opentorah.xml.ScalaXml
import java.net.URI

abstract class Report[T](val name: String, val title: String) extends Store with HtmlContent[Site] {
  final override def names: Names = Names(name)
  final override def htmlHeadTitle: Option[String] = Some(title)
  final override def htmlBodyTitle: Option[ScalaXml.Nodes] = htmlHeadTitle.map(ScalaXml.mkText)

  final override def content(site: Site): Caching.Parser[ScalaXml.Element] =
    lines(site).map(lines => <div>{lines.map(line => lineToXml(line, site))}</div>)

  protected def lines(site: Site): Caching.Parser[Seq[T]]

  protected def lineToXml(line: T, site: Site): ScalaXml.Element
}

object Report {

  object NoRefs extends Report[WithSource[EntityReference]](
    "no-refs",
    "Имена без атрибута /ref/"
  ) {
    override protected def lines(site: Site): Caching.Parser[Seq[WithSource[EntityReference]]] = site.getReferences.map(_
      .filter(_.value.ref.isEmpty)
      .sortBy(reference => ScalaXml.toString(reference.value.name).toLowerCase)
    )

    override protected def lineToXml(reference: WithSource[EntityReference], site: Site): ScalaXml.Element = {
      val source: String = reference.source
      <l>{ScalaXml.toString(reference.value.name)} в {html.a(new URI(source))(text = source)}</l>
    }
  }

  object Unclears extends Report[WithSource[Unclear.Value]](
    "unclears",
    "Неясности"
  ) {
    override protected def lines(site: Site): Caching.Parser[Seq[WithSource[Unclear.Value]]] =
      site.getUnclears.map(_.sortBy(_.source))

    override protected def lineToXml(unclear: WithSource[Unclear.Value], site: Site): ScalaXml.Element = {
      val source: String = unclear.source
      <l>{ScalaXml.toString(unclear.value.content)} в {html.a(new URI(source))(text = source)}</l>
    }
  }

  object MisnamedEntities extends Report[Entity](
    "misnamed-entities",
    "Неправильно названные файлы с именами"
  ) {
    override protected def lines(site: Site): Caching.Parser[Seq[Entity]] = site.entities.directoryEntries.map(_
      .filterNot(entity => entity.id == getExpectedId(entity))
      .sortBy(_.name)
    )

    override protected def lineToXml(entity: Entity, site: Site): ScalaXml.Element =
      <l>{entity.a(site)(text = entity.id)} {s"должен по идее называться '${getExpectedId(entity)}'"}</l>

    private def getExpectedId(entity: Entity): String = Strings.spacesToUnderscores(entity.mainName)
  }
}