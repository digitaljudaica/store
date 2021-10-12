package org.opentorah.texts.tanach

import org.opentorah.metadata.{Language, LanguageSpec}

final case class Span(from: Verse, to: Verse) extends Language.ToString:
  require(from <= to, s"Empty span: $from..$to")

  def contains(verse: Verse): Boolean = (from <= verse) && (verse <= to)

  override def toLanguageString(using spec: LanguageSpec): String =
    if from.chapter != to.chapter then from.toLanguageString + "-" + to.toLanguageString
    else spec.toString(from.chapter) + ":" +
      (if from.verse == to.verse then spec.toString(from.verse)
      else spec.toString(from.verse) + "-" + spec.toString(to.verse))
