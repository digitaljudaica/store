package org.podval.calendar.jewish

import Jewish.{Year, Month, Day}
import Month.Name._

trait SpecialDay {
  def apply(year: Year): Day
}

class SpecialDayBase(month: Month.Name, numberInMonth: Int) extends SpecialDay {
  final def apply(year: Year): Day = year.month(month).day(numberInMonth)
}

object SpecialDay {
  case object RoshHashanah extends SpecialDayBase(Tishrei, 1)
  case object RoshHashanah2 extends SpecialDayBase(Tishrei, 2)
  case object YomKippur extends SpecialDayBase(Tishrei, 10)

  case object Sukkot extends SpecialDayBase(Tishrei, 15)
  case object Sukkot2 extends SpecialDayBase(Tishrei, 16)
  case object SukkotIntermediate1 extends SpecialDayBase(Tishrei, 17)
  case object SukkotIntermediate2 extends SpecialDayBase(Tishrei, 18)
  case object SukkotIntermediate3 extends SpecialDayBase(Tishrei, 19)
  case object SukkotIntermediate4 extends SpecialDayBase(Tishrei, 20)
  case object SukkotIntermediate5 extends SpecialDayBase(Tishrei, 21)
  case object HoshanahRabbah extends SpecialDayBase(Tishrei, 21)
  case object ShminiAtzeret extends SpecialDayBase(Tishrei, 22)
  case object SimchatTorah extends SpecialDayBase(Tishrei, 23)

  case object SukkotIntermediate1InHolyLand extends SpecialDayBase(Tishrei, 16)
  case object SukkotIntermediate2InHolyLand extends SpecialDayBase(Tishrei, 17)
  case object SukkotIntermediate3InHolyLand extends SpecialDayBase(Tishrei, 18)
  case object SukkotIntermediate4InHolyLand extends SpecialDayBase(Tishrei, 19)
  case object SukkotIntermediate5InHolyLand extends SpecialDayBase(Tishrei, 20)
  case object SukkotIntermediate6InHolyLand extends SpecialDayBase(Tishrei, 21)
  case object ShminiAtzeretAndSimchatTorahInHolyLand extends SpecialDayBase(Tishrei, 22)

  case object ShabbosBereshit extends SpecialDay {
    override def apply(year: Year): Day = shabbosAfter(SimchatTorah(year))
  }

  case object Hanukkah extends SpecialDayBase(Kislev, 25)

  case object FastOfEster extends SpecialDay {
    override def apply(year: Year): Day = Purim(year).prev
  }

  case object Purim extends SpecialDay {
    override def apply(year: Year): Day = year.month(if (year.isLeap) AdarII else Adar).day(14)
  }

  case object ShushanPurim extends SpecialDay {
    override def apply(year: Year): Day = Purim(year).next
  }

  case object Pesach extends SpecialDayBase(Nisan, 15)
  case object Pesach2 extends SpecialDayBase(Nisan, 16)

  case object PesachIntermediate1 extends SpecialDayBase(Nisan, 17)
  case object PesachIntermediate2 extends SpecialDayBase(Nisan, 18)
  case object PesachIntermediate3 extends SpecialDayBase(Nisan, 19)
  case object PesachIntermediate4 extends SpecialDayBase(Nisan, 20)
  case object ShviiShelPesach extends SpecialDayBase(Nisan, 21)
  case object AcharonShelPesach extends SpecialDayBase(Nisan, 22)

  case object PesachIntermediate1InHolyLand extends SpecialDayBase(Nisan, 16)
  case object PesachIntermediate2InHolyLand extends SpecialDayBase(Nisan, 17)
  case object PesachIntermediate3InHolyLand extends SpecialDayBase(Nisan, 18)
  case object PesachIntermediate4InHolyLand extends SpecialDayBase(Nisan, 19)
  case object PesachIntermediate5InHolyLand extends SpecialDayBase(Nisan, 20)
  case object ShviiAndAcharonShelPesachInHolyLand extends SpecialDayBase(Nisan, 21)

  case object LagBaOmer extends SpecialDayBase(Iyar, 18)

  case object Shavuot extends SpecialDayBase(Sivan, 6)
  case object Shavuot2 extends SpecialDayBase(Sivan, 7)

  case object FastOfTammuz extends  SpecialDayBase(Tammuz, 17)

  case object TishaBav extends SpecialDayBase(Av, 9)


  val festivals: Seq[SpecialDayBase] = Seq(
    RoshHashanah,
    RoshHashanah2,
    YomKippur,
    Sukkot,
    Sukkot2,
    SukkotIntermediate1,
    SukkotIntermediate2,
    SukkotIntermediate3,
    SukkotIntermediate4,
    SukkotIntermediate5,
    ShminiAtzeret,
    SimchatTorah,
    Pesach,
    Pesach2,
    PesachIntermediate1,
    PesachIntermediate2,
    PesachIntermediate3,
    PesachIntermediate4,
    ShviiShelPesach,
    AcharonShelPesach,
    Shavuot,
    Shavuot2
  )

  val festivalsInHolyLand: Seq[SpecialDayBase] = Seq(
    RoshHashanah,
    RoshHashanah2,
    YomKippur,
    Sukkot,
    SukkotIntermediate1InHolyLand,
    SukkotIntermediate2InHolyLand,
    SukkotIntermediate3InHolyLand,
    SukkotIntermediate4InHolyLand,
    SukkotIntermediate5InHolyLand,
    SukkotIntermediate6InHolyLand,
    ShminiAtzeretAndSimchatTorahInHolyLand,
    Pesach,
    Pesach2,
    PesachIntermediate1InHolyLand,
    PesachIntermediate2InHolyLand,
    PesachIntermediate3InHolyLand,
    PesachIntermediate4InHolyLand,
    PesachIntermediate5InHolyLand,
    ShviiAndAcharonShelPesachInHolyLand,
    Shavuot
  )

  def festivals(inHolyLand: Boolean): Seq[SpecialDayBase] = if (inHolyLand) festivalsInHolyLand else festivals


  // TODO for generating Siddur, we need a list of days when Tachanun is not said;
  // also, when Av Harachamim is not said on Shabbos (by the way, when Tisha Bav is on Shabbos, it *is* said,
  // although we wouldn't have said Tachanun if it wasn't Shabbos...)

  def shabbosAfter(day: Day): Day = day.next.next(Day.Name.Shabbos)

  def shabbosBefore(day: Day): Day = day.prev.prev(Day.Name.Shabbos)
}
