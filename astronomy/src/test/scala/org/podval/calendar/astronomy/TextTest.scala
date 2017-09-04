package org.podval.calendar.astronomy

import org.scalatest.FlatSpec
import org.podval.calendar.angle.AngleNumberSystem
import AngleNumberSystem.{Angle, AnglePoint, headRange, range}
import Zodiac.{Constellation, constellations}
import org.podval.calendar.jewish.{Cycle, Jewish}
import Jewish.{Year, Month, Day}

@org.junit.runner.RunWith(classOf[org.scalatest.junit.JUnitRunner])
class TextTest extends FlatSpec {
  "angle units" should "be as in KH 11:7" in {
    assertResult(360)(headRange)
    assertResult( 60)(range(0))
    assertResult( 60)(range(1))
    assertResult( 60)(range(2))
    assertResult( 60)(range(3))
    assertResult( 60)(range(4))
    assertResult( 60)(range(5))
    assertResult( 60)(range(6))
  }

  "zodiac" should "be as in KH 11:7-9" in {
    assertResult(12)(constellations.length)
    assertResult(Angle(0))(Zodiac.Aries.start)
    constellations.init.zip(constellations.tail).foreach {
      case (prev: Constellation, next: Constellation) =>
        assertResult(prev.end)((prev.start + Angle(30)).normal)
        assertResult(next.start)(prev.end)
    }

    val (constellation1: Zodiac.Constellation, angle1: Angle) = Zodiac.fromAngle(AnglePoint(70, 30, 40))
    assertResult(Zodiac.Gemini)(constellation1)
    assertResult(Angle(10, 30, 40))(angle1)

    val (constellation2: Zodiac.Constellation, angle2: Angle) = Zodiac.fromAngle(AnglePoint(320))
    assertResult(Zodiac.Aquarius)(constellation2)
    assertResult(Angle(20))(angle2)
  }

  "angles" should "subtract as in KH 11:12" in {
    assertResult(AnglePoint(259, 29, 50))((AnglePoint(100, 20, 30) - Angle(200, 50, 40)).normal.canonical)
  }

  "example day" should "be as in KH 11:16" in {
    assertResult(4938)(Cycle.yearInCycle(260, 17))
    assertResult(260)(Cycle.yearCycle(4938))
    assertResult(17)(Cycle.yearNumberInCycle(4938))
    assertResult(Year(4938).month(Month.Name.Nisan).day(3))(Epoch.epoch)
    assertResult(Day.Name.Chamishi)(Epoch.epoch.name)
  }

  "example Sun calculations" should "be as in KH 12:2" in {
    val nextDay: Day = Year(4938).month(Month.Name.Tammuz).day(14)
    assertResult(100)(nextDay.number - Epoch.epoch.number)
    assertResult(Day.Name.Shabbos)(nextDay.name)
    val nextLongitude: AnglePoint = SunLongitudeMean.atEpoch + SunLongitudeMean.value(100)
    assertResult(AnglePoint(105, 37, 25))(nextLongitude)
    val (constellation, angle) = Zodiac.fromAngle(nextLongitude)
    assertResult(Zodiac.Cancer)(constellation)
    assertResult(Angle(15, 37, 25))(angle)
  }
}
