package org.opentorah.texts.tanach

import org.opentorah.metadata.WithNumber
import org.opentorah.store.{By, Selector, Stores}
import org.opentorah.xml.Parser

final class Chapters(chapters: Seq[Int]) extends By with Stores.Numbered[Chapter] {
  override def selector: Selector = Selector.byName("chapter")
  override def length: Int = chapters.length
  override protected def createNumberedStore(number: Int): Chapter = new Chapter(number, length(number))

  def length(chapter: Int): Int = chapters(chapter-1)

  def next(verse: Verse): Option[Verse] = {
    require(contains(verse))
    if (verse.verse < length(verse.chapter))
      Some(Verse(verse.chapter, verse.verse+1))
    else if (verse.chapter+1 <= chapters.length)
      Some(Verse(verse.chapter+1, 1))
    else
      None
  }

  def prev(verse: Verse): Option[Verse] = {
    require(contains(verse))
    if (verse.verse > 1)
      Some(Verse(verse.chapter, verse.verse-1))
    else if (verse.chapter-1 >= 1)
      Some(Verse(verse.chapter-1, length(verse.chapter-1)))
    else
      None
  }

  def first: Verse = Verse(1, 1)

  def last: Verse = Verse(chapters.length, length(chapters.length))

  def full: Span = Span(first, last)

  def contains(span: Span): Boolean = contains(span.from) && contains(span.to)

  def contains(verse: Verse): Boolean =
    (verse.chapter <= chapters.length) && (verse.verse <= length(verse.chapter))

  def consecutive(first: Span, second: Span): Boolean = {
    require(contains(first))
    require(contains(second))
    val nextVerse = next(first.to)
    nextVerse.fold(false)(_ == second.from)
  }

  def consecutive(spans: Seq[Span]): Boolean =
    spans.zip(spans.tail).forall { case (first, second) => consecutive(first, second) }

  def merge(first: Span, second: Span): Span = {
    require(consecutive(first, second))
    Span(first.from, second.to)
  }

  def cover(spans: Seq[Span], span: Span): Boolean = {
    require(contains(span))
    consecutive(spans) && (spans.head.from == span.from) && (spans.last.to == span.to)
  }
}

object Chapters {

  val parser: Parser[Chapters] = for {
    chapters <- Chapter.seq()
    _ <- WithNumber.checkConsecutive(chapters, "chapter")
  } yield new Chapters(WithNumber.dropNumbers(chapters))
}
