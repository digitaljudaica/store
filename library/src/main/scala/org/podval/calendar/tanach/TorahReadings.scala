package org.podval.calendar.tanach

import org.podval.judaica.metadata.{Attributes, WithNames, XML}
import org.podval.judaica.tanach.{SpanParsed, Torah, WithNumber}

import scala.xml.Elem

trait TorahReadings {
  protected final def parseTorahForShabbosAndWeekday(
    source: WithNames,
    drop1: Int,
    drop2: Int,
    element: Elem
  ): (Torah, Torah) =
    parseTorahForShabbosAndWeekday(source, Set(drop1, drop2), element)

  protected final def parseTorahForShabbosAndWeekday(
    source: WithNames,
    toDrop: Set[Int],
    element: Elem
  ): (Torah, Torah) = {
    val shabbosAliyot: Torah = parseTorah(source, element)
    (shabbosAliyot, shabbosAliyot.drop(toDrop))
  }

  protected final def parseTorah(
    source: WithNames,
    element: Elem
  ): Torah = {
    val (attributes: Attributes, elements: Seq[Elem]) = XML.open(element, "torah")
    val bookSpan: Torah.Fragment = Torah.parseSpan(attributes).resolve
    val fromChapter: Int = bookSpan.span.from.chapter
    val result: Seq[Torah.Numbered] =
      elements.map(element => XML.parseEmpty(element, "aliyah", parseNumbered(fromChapter)))
    Torah.parseAliyot(bookSpan, result, number = None).fromWithNumbers(source)
  }

  private def parseNumbered(fromChapter: Int)(attributes: Attributes): Torah.Numbered = {
    val result = WithNumber.parse(attributes,
      attributes => SpanParsed.parse(attributes).defaultFromChapter(fromChapter).semiResolve)
    require(result.what.to.isEmpty, s"Non-empty to: ${result.what}")
    result
  }

  protected final def parseMaftir(element: Elem): Torah.Maftir = {
    val attributes = XML.openEmpty(element, "maftir")
    Torah.parseSpan(attributes).resolve
  }
}
