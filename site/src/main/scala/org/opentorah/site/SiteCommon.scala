package org.opentorah.site

import org.opentorah.metadata.Names
import org.opentorah.xml.{Attribute, Element, Parsable, Parser, RawXml, Unparser}

final class SiteCommon(
  val names: Names,
  val url: Option[String],
  val favicon: Option[String],
  val googleAnalyticsId: Option[String],
  val email: Option[String],
  val title: Option[SiteCommon.Title.Value],
  val license: Option[SiteLicense],
  val social: Option[SiteSocial],
  val footer: Option[SiteCommon.Footer.Value],
  val pages: Seq[String],
  val docbook: Seq[SiteDocBook],
  private val tei: Option[SiteTei],
  private val highlighter: Option[SiteHighlighter],
  private val mathJax: Option[SiteMathJax]
):
  def getSocial     : SiteSocial      = social     .getOrElse(SiteSocial     .empty)
  def getTei        : SiteTei         = tei        .getOrElse(SiteTei        .empty)
  def getHighlighter: SiteHighlighter = highlighter.getOrElse(SiteHighlighter.empty)
  def getMathJax    : SiteMathJax     = mathJax    .getOrElse(SiteMathJax    .empty)

object SiteCommon extends Element[SiteCommon]("common"):
  object Title  extends RawXml("title")
  object Footer extends RawXml("footer")

  override def contentParsable: Parsable[SiteCommon] = new Parsable[SiteCommon]:
    private val urlAttribute: Attribute.Optional[String] = Attribute("url").optional
    private val faviconAttribute: Attribute.Optional[String] = Attribute("favicon").optional
    private val googleAnalyticsIdAttribute: Attribute.Optional[String] = Attribute("googleAnalyticsId").optional
    private val emailAttribute: Attribute.Optional[String] = Attribute("email").optional
  
    override def parser: Parser[SiteCommon] = for
      names: Names <- Names.withDefaultNameParsable()
      url: Option[String] <- urlAttribute()
      favicon: Option[String] <- faviconAttribute()
      googleAnalyticsId: Option[String] <- googleAnalyticsIdAttribute()
      email: Option[String] <- emailAttribute()
      title: Option[SiteCommon.Title.Value] <- Title.element.optional()
      license: Option[SiteLicense] <- SiteLicense.optional()
      social: Option[SiteSocial] <- SiteSocial.optional()
      footer: Option[SiteCommon.Footer.Value] <- Footer.element.optional()
      pages: Seq[String] <- SitePage.seq()
      tei: Option[SiteTei] <- SiteTei.optional()
      docbook: Seq[SiteDocBook] <- SiteDocBook.seq()
      highlighter: Option[SiteHighlighter] <- SiteHighlighter.optional()
      mathJax: Option[SiteMathJax] <- SiteMathJax.optional()
    yield SiteCommon(
      names,
      url,
      favicon,
      googleAnalyticsId,
      email,
      title,
      license,
      social,
      footer,
      pages,
      docbook,
      tei,
      highlighter,
      mathJax
    )

    override def unparser: Unparser[SiteCommon] = Unparser.concat[SiteCommon](
      Names.withDefaultNameParsable(_.names),
      urlAttribute(_.url),
      faviconAttribute(_.favicon),
      googleAnalyticsIdAttribute(_.googleAnalyticsId),
      emailAttribute(_.email),
      Title.element.optional(_.title),
      SiteLicense.optional(_.license),
      SiteSocial.optional(_.social),
      Footer.element.optional(_.footer),
      SitePage.seq(_.pages),
      SiteDocBook.seq(_.docbook),
      SiteTei.optional(_.tei),
      SiteHighlighter.optional(_.highlighter),
      SiteMathJax.optional(_.mathJax)
    )
