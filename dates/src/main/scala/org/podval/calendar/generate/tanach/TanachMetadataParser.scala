package org.podval.calendar.generate.tanach

import java.net.URL

import scala.xml.Elem
import Tanach.{ChumashBook, ChumashBookStructure, NachBook, NachBookStructure}
import org.podval.calendar.metadata.MetadataParser.{MetadataPreparsed, bind}
import org.podval.calendar.metadata.{MetadataParser, Named, XML}

object TanachMetadataParser {
  def parse(obj: AnyRef): (
    Map[ChumashBook, ChumashBookStructure],
    Map[NachBook, NachBookStructure]
  ) = {
    val className: String = Named.className(obj)
    val url = MetadataParser.getUrl(obj, className)

    val books: Seq[MetadataPreparsed] = MetadataParser.loadMetadataResource(url, className, "book")

    (
      bind(Tanach.chumash, books.take(Tanach.chumash.length)).map(parseChumashBook(url, _)).toMap,
      bind(Tanach.nach, books.drop(Tanach.chumash.length)).map(parseNachBook).toMap
    )
  }

  private def parseNachBook(arg: (NachBook, MetadataPreparsed)): (NachBook, NachBookStructure) = {
    val (book: NachBook, metadata: MetadataPreparsed) = arg
    metadata.attributes.close()
    val (chapterElements, tail) = XML.span(metadata.elements, "chapter")
    XML.checkNoMoreElements(tail)
    val result = new NachBookStructure(
      book,
      metadata.names,
      chapters = parseChapters(book, chapterElements)
    )
    book -> result
  }

  private final case class ChapterParsed(n: Int, length: Int)

  private def parseChapters(book: Tanach.Book[_, _], elements: Seq[Elem]): Chapters = {
    val chapters: Seq[ChapterParsed] = elements.map { element =>
      val attributes = XML.openEmpty(element, "chapter" )
      val result = ChapterParsed(
        n = attributes.doGetInt("n"),
        length = attributes.doGetInt("length")
      )
      attributes.close()
      result
    }

    require(chapters.map(_.n) == (1 to chapters.length), "Wrong chapter numbers.")

    new Chapters(chapters.map(_.length).toArray)
  }

  private def parseChumashBook(
    url: URL,
    arg: (ChumashBook, MetadataPreparsed)
  ): (ChumashBook,  ChumashBookStructure) = {
    val (book: ChumashBook, metadata: MetadataPreparsed) = arg
    metadata.attributes.close()
    // TODO handle names from metadata
    val (chapterElements, weekElements) = XML.span(metadata.elements, "chapter", "week")
    val chapters: Chapters = parseChapters(book, chapterElements)

    val weeks: Seq[(Parsha, Parsha.Structure)] = ParshaMetadataParser.parse(
      metadata = MetadataParser.loadMetadata(url, weekElements, "week"),
      parshiot = Parsha.forBook(book),
      chapters = chapters
    )

    val result = new ChumashBookStructure(
      book,
      weeks.head._2.names,
      chapters,
      weeks.toMap
    )

    book -> result
  }
}
