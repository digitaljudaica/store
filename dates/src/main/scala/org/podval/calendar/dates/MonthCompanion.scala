package org.podval.calendar.dates

/**
  *
  */
abstract class MonthCompanion[C <: Calendar[C]] extends CalendarMember[C] {
  def apply(number: Int): C#Month

  final def apply(year: Int, monthInYear: Int): C#Month =
    calendar.Year(year).month(monthInYear)

  def yearNumber(monthNumber: Int): Int

  def numberInYear(monthNumber: Int): Int
}
