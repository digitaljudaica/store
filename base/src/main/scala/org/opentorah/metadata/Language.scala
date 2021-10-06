package org.opentorah.metadata

sealed class Language(code: String) extends Named:
  final override def names: Names = Language.toNames(this)

  final override def name: String = code

  final def toSpec: LanguageSpec = new LanguageSpec(language = Some(this), isTransliterated = None, flavour = None)

  def toString(number: Int): String = number.toString

object Language extends NamedCompanion:

  trait ToString:
    final override def toString: String = toLanguageString(using LanguageSpec.empty)
    def toLanguageString(using spec: LanguageSpec): String

  override type Key = Language

  override val values: Seq[Language] = Seq(English, Russian, Polish, French, German, Lithuanian, Hebrew)

  case object English extends Language("en")
  case object Russian extends Language("ru")
  case object Polish extends Language("pl")
  case object French extends Language("fr")
  case object German extends Language("de")
  case object Lithuanian extends Language("lt")

  case object Hebrew extends Language("he"):
    val MAQAF: Char       = '־'
    val PASEQ: Char       = '׀'
    val SOF_PASUQ: Char   = '׃'

    private val units: List[Char] = "אבגדהוזחט".toList
    private val decades: List[Char] = "יכלמנסעפצ".toList
    private val hundreds: List[Char] = "קרשת".toList

    override def toString(number: Int): String =
// to display 0 as empty string :)      require(number > 0)
      require(number <= 10000)

      val result = new StringBuilder
      var remainder = number

      if remainder >= 1000 then
        result.append(units((remainder / 1000) - 1))
        result.append("׳")
        remainder = remainder % 1000

      if remainder >= 900 then
        result.append("תת")
        remainder = remainder - 800

      if remainder >= 500 then
        result.append("ת")
        remainder = remainder - 400

      if remainder >= 100 then
        result.append(hundreds((remainder / 100) - 1))
        remainder = remainder % 100

      if remainder == 15 then result.append("טו") else
      if remainder == 16 then result.append("טז") else
        if remainder >= 10 then
          result.append(decades((remainder / 10) - 1))
          remainder = remainder % 10

        if remainder >= 1 then result.append(units(remainder - 1))

      result.toString
