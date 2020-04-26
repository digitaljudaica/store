package org.opentorah.judaica.tanach

import org.opentorah.metadata.{Named, NamedCompanion, Names}

sealed trait Parsha extends Named {
  def book: Tanach.ChumashBook

  private def metadata: ParshaMetadata = book.metadata.forParsha(this)

  final override def names: Names = metadata.names

  final def span: Span = metadata.span

  final def days: Torah.Customs = metadata.days

  final def daysCombined: Option[Torah.Customs] = metadata.daysCombined

  final def getDaysCombined: Torah.Customs = {
    require(this.combines)
    daysCombined.get
  }

  final def aliyot: Torah = metadata.aliyot

  final def maftir: Torah.Maftir = metadata.maftir

  final def combines: Boolean = Parsha.combinable.contains(this)
}

object Parsha extends NamedCompanion {
  override type Key = Parsha

  trait GenesisParsha extends Parsha { final override def book: Tanach.ChumashBook = Tanach.Genesis }

  case object Bereishis extends GenesisParsha
  case object Noach extends GenesisParsha
  case object LechLecha extends GenesisParsha { override def name: String = "Lech Lecha" }
  case object Vayeira extends GenesisParsha
  case object ChayeiSarah extends GenesisParsha { override def name: String = "Chayei Sarah" }
  case object Toldos extends GenesisParsha
  case object Vayeitzei extends GenesisParsha
  case object Vayishlach extends GenesisParsha
  case object Vayeishev extends GenesisParsha
  case object Mikeitz extends GenesisParsha
  case object Vayigash extends GenesisParsha
  case object Vayechi extends GenesisParsha

  val genesis: Seq[Parsha] = Seq(Bereishis, Noach, LechLecha, Vayeira, ChayeiSarah, Toldos,
    Vayeitzei, Vayishlach, Vayeishev, Mikeitz, Vayigash, Vayechi)

  trait ExodusParsha extends Parsha { final override def book: Tanach.ChumashBook = Tanach.Exodus }

  case object Shemos extends ExodusParsha
  case object Va_eira extends ExodusParsha { override def name: String = "Va'eira" }
  case object Bo extends ExodusParsha
  case object Beshalach extends ExodusParsha
  case object Yisro extends ExodusParsha
  case object Mishpatim extends ExodusParsha
  case object Terumah extends ExodusParsha
  case object Tetzaveh extends ExodusParsha
  case object KiSisa extends ExodusParsha { override def name: String = "Ki Sisa" }
  case object Vayakhel extends ExodusParsha
  case object Pekudei extends ExodusParsha

  val exodus: Seq[Parsha] = Seq(Shemos, Va_eira, Bo, Beshalach, Yisro, Mishpatim, Terumah,
    Tetzaveh, KiSisa, Vayakhel, Pekudei)

  trait LeviticusParsha extends Parsha { final override def book: Tanach.ChumashBook = Tanach.Leviticus }

  case object Vayikra extends LeviticusParsha
  case object Tzav extends LeviticusParsha
  case object Shemini extends LeviticusParsha
  case object Tazria extends LeviticusParsha
  case object Metzora extends LeviticusParsha
  case object Acharei extends LeviticusParsha
  case object Kedoshim extends LeviticusParsha
  case object Emor extends LeviticusParsha
  case object Behar extends LeviticusParsha
  case object Bechukosai extends LeviticusParsha

  val leviticus: Seq[Parsha] = Seq(Vayikra, Tzav, Shemini, Tazria, Metzora, Acharei, Kedoshim, Emor, Behar, Bechukosai)

  trait NumbersParsha extends Parsha { final override def book: Tanach.ChumashBook = Tanach.Numbers }

  case object Bemidbar extends NumbersParsha
  case object Nasso extends NumbersParsha
  case object Beha_aloscha extends NumbersParsha { override def name: String = "Beha'aloscha" }
  case object Shelach extends NumbersParsha
  case object Korach extends NumbersParsha
  case object Chukas extends NumbersParsha
  case object Balak extends NumbersParsha
  case object Pinchas extends NumbersParsha
  case object Mattos extends NumbersParsha
  case object Masei extends NumbersParsha

  val numbers: Seq[Parsha] = Seq(Bemidbar, Nasso, Beha_aloscha, Shelach, Korach, Chukas, Balak, Pinchas, Mattos, Masei)

  trait DeutoronomyParsha extends Parsha { final override def book: Tanach.ChumashBook = Tanach.Deuteronomy }

  case object Devarim extends DeutoronomyParsha
  case object Va_eschanan extends DeutoronomyParsha { override def name: String = "Va'eschanan" }
  case object Eikev extends DeutoronomyParsha
  case object Re_eh extends DeutoronomyParsha { override def name: String = "Re'eh" }
  case object Shoftim extends DeutoronomyParsha
  case object KiSeitzei extends DeutoronomyParsha { override def name: String = "Ki Seitzei" }
  case object KiSavo extends DeutoronomyParsha { override def name: String = "Ki Savo" }
  case object Nitzavim extends DeutoronomyParsha
  case object Vayeilech extends DeutoronomyParsha
  case object Haazinu extends DeutoronomyParsha
  case object VezosHaberachah extends DeutoronomyParsha { override def name: String = "Vezos Haberachah" }

  val deuteronomy: Seq[Parsha] = Seq(Devarim, Va_eschanan, Eikev, Re_eh, Shoftim, KiSeitzei, KiSavo,
    Nitzavim, Vayeilech, Haazinu, VezosHaberachah)

  final override val values: Seq[Parsha] = genesis ++ exodus ++ leviticus ++ numbers ++ deuteronomy

  // Rules of combining; affect the WeeklyReading.
  final val combinableFromBereishisToVayikra: Seq[Parsha] = Seq(Vayakhel)
  // Reversing the priorities here currently affects only non-leap regular years with Rosh
  // Hashanah on Thursday (and Pesach on Shabbat).
  final val combinableFromVayikraToBemidbar: Seq[Parsha] = Seq(Tazria, Acharei, Behar)
  final val combinableFromBemidbarToVa_eschanan: Seq[Parsha] = Seq(Mattos, Chukas)
  final val combinableFromVa_eschanan: Seq[Parsha] = Seq(Nitzavim)

  final val combinable: Set[Parsha] = (combinableFromBereishisToVayikra ++ combinableFromVayikraToBemidbar ++
    combinableFromBemidbarToVa_eschanan ++ combinableFromVa_eschanan).toSet
}
