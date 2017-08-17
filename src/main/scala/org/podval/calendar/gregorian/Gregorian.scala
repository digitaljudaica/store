package org.podval.calendar.gregorian

import org.podval.calendar.calendar._

class Gregorian private() extends Calendar[Gregorian] {

  trait GregorianCalendarMember extends CalendarMember[Gregorian] {
    final override def calendar: Gregorian = Gregorian.this
  }

  final class Year(number: Int)
    extends YearBase(number) with GregorianCalendarMember
  {
    override def firstDayNumber: Int = Year.firstDay(number)

    override def lengthInDays: Int = Year.lengthInDays(number)

    override def character: YearCharacter = isLeap
  }

  final override def createYear(number: Int): Year = new Year(number)

  final override type YearCharacter = Boolean

  object Year extends YearCompanion {
    protected override def characters: Seq[YearCharacter] = Seq(true, false)

    protected override def monthNamesAndLengths(isLeap: YearCharacter): List[MonthNameAndLength] = {
      import MonthName._
      List(
        MonthNameAndLength(January  , 31),
        MonthNameAndLength(February , if (isLeap) 29 else 28),
        MonthNameAndLength(March    , 31),
        MonthNameAndLength(April    , 30),
        MonthNameAndLength(May      , 31),
        MonthNameAndLength(June     , 30),
        MonthNameAndLength(July     , 31),
        MonthNameAndLength(August   , 31),
        MonthNameAndLength(September, 30),
        MonthNameAndLength(October  , 31),
        MonthNameAndLength(November , 30),
        MonthNameAndLength(December , 31)
      )
    }

    protected override def areYearsPositive: Boolean = false

    override def isLeap(yearNumber: Int): Boolean = (yearNumber % 4 == 0) && ((yearNumber % 100 != 0) || (yearNumber % 400 == 0))

    override def firstMonth(yearNumber: Int): Int = monthsInYear*(yearNumber - 1) + 1

    override def lengthInMonths(yearNumber: Int): Int = monthsInYear

    val monthsInYear: Int = 12

    private val daysInNonLeapYear: Int = 365

    def firstDay(yearNumber: Int): Int = daysInNonLeapYear * (yearNumber - 1) + (yearNumber - 1)/4 - (yearNumber - 1)/100 + (yearNumber - 1)/400 + 1

    def lengthInDays(yearNumber: Int): Int = if (Gregorian.Year.isLeap(yearNumber)) daysInNonLeapYear + 1 else daysInNonLeapYear
  }


  final override type Month = GregorianMonth

  final override def createMonth(number: Int): Month = new GregorianMonth(number) with GregorianCalendarMember

  final override type MonthName = GregorianMonthName

  // TODO stick it into the Month companion???
  val MonthName: GregorianMonthName.type = GregorianMonthName

  object Month extends MonthCompanion {
    override def yearNumber(monthNumber: Int): Int = (monthNumber - 1) / Gregorian.Year.monthsInYear + 1

    override def numberInYear(monthNumber: Int): Int =  monthNumber - Gregorian.Year.firstMonth(yearNumber(monthNumber)) + 1
  }


  final override type Day = GregorianDay

  final override def createDay(number: Int): Day = new GregorianDay(number) with GregorianCalendarMember

  final override type DayName = GregorianDayName

  // TODO stick it into the Day companion???
  val DayName: GregorianDayName.type = GregorianDayName

  final override val Day: GregorianDayCompanion = new GregorianDayCompanion with GregorianCalendarMember


  final class Moment(negative: Boolean, digits: List[Int])
    extends MomentBase(negative, digits) with GregorianCalendarMember
  {
    def morningHours(value: Int): Moment = firstHalfHours(value)

    def afternoonHours(value: Int): Moment = secondHalfHours(value)
  }

  final override def createMoment(negative: Boolean, digits: List[Int]): Moment =
    new Moment(negative, digits)

  object Moment extends MomentCompanion
}


object Gregorian extends Gregorian
