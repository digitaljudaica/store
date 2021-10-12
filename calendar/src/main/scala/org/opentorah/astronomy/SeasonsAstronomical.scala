package org.opentorah.astronomy

import org.opentorah.angles.Angles.{Position, Rotation}
import org.opentorah.calendar.jewish.{Jewish, Season}
import Jewish.{Moment, Month, Year}
import org.opentorah.numbers.Zeroable

// In KH 13:11, calculation of true solstices/equinoxes is mentioned, but no algorithm is given.
final class SeasonsAstronomical(calculator: Calculator) extends Season.ForYear:
  override def seasonForYear(season: Season, year: Year): Moment =
    val zodiac: Zodiac = season match
      case Season.TkufasNisan   => Zodiac.Aries
      case Season.TkufasTammuz  => Zodiac.Cancer
      case Season.TkufasTishrei => Zodiac.Libra
      case Season.TkufasTeves   => Zodiac.Capricorn

    def f(moment: Moment): Rotation = sunLongitudeTrue(moment) - zodiac.start
    val left: Moment = year.month(Month.Nisan).prev.firstDay.toMoment
    val right: Moment = year.month(Month.Nisan).next.firstDay.toMoment
    val result: Moment = Zeroable.findZero[Rotation](Jewish)(f, left, right, 0)
    result

  private def sunLongitudeTrue(moment: Moment): Position = calculator.calculate(moment.day).sunLongitudeTrue
