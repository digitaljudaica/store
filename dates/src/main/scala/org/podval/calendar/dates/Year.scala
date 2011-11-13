/*
 * Copyright 2011 Podval Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.podval.calendar.dates


final class Year(val number: Int) {

    require(0 < number)


    override def equals(other: Any): Boolean = other match {
        case that: Year => (number == that.number)
        case _ => false
    }


    override def hashCode = number


    def cycle: Int = Year.cycle(number)


    def numberInCycle: Int = Year.numberInCycle(number)


    def isLeap: Boolean = Year.isLeapYearInCycle(numberInCycle)


    def lengthInMonth = Year.lengthInMonth(numberInCycle)


    def monthsBeforeInCycle: Int = {
        val result = Year.monthsBeforeYearInCycle(numberInCycle)
        // First year woth 0 month long!
        if (cycle == 1) result - Year.lengthInMonth(1) else result
    }

    
    // TODO do I need to take into account the first cycle here also?
    def monthsBefore: Int = Year.MonthsInCycle*(cycle-1) + monthsBeforeInCycle


    def month(month: Int): Month = Month(this, month)


    def length() = Year(number+1).dayOfRoshHaShono.number - dayOfRoshHaShono.number


    def dayOfRoshHaShono: Day = {
        val newMoon = month(1).newMoon
        val day = newMoon.day

        if (Year.isAdu(day)) day.next // KH 7:1
        else if (newMoon.time.notEarlierThan(18, 0)) {
            val delayed = day.next
            if (!Year.isAdu(delayed))
            delayed /* KH 7:2 */ else
            delayed.next /* KH 7:3 */
        }
        else if ((day.dayOfWeek == 3) && newMoon.time.notEarlierThan(9, 240) &&
            !this.isLeap) day.next.next /* KH 7:4 */
        else if ((day.dayOfWeek == 2) && newMoon.time.notEarlierThan(15, 589) &&
            this.next.isLeap) day.next /* KH 7:5 */
        else day
    }


    // KH 8:7,8
    def kind: Year.Kind.Type = {
        val length = this.length

        def impossible = new IllegalArgumentException("Impossible year length " + length + " for " + this)

        if (!isLeap) {
            if (length == 353) Year.Kind.Short else
            if (length == 354) Year.Kind.Regular else
            if (length == 355) Year.Kind.Full else
                throw impossible
        } else {
            if (length == 383) Year.Kind.Short else
            if (length == 384) Year.Kind.Regular else
            if (length == 385) Year.Kind.Full else
                throw impossible
        }
    }


    def next: Year = Year(number + 1)


    def prev: Year = Year(number - 1)
}


object Year {

    val YearsInCycle = 19;


    val LeapYears = List(3, 6, 8, 11, 14, 17, 19)


    val LeapYearsInCycle = LeapYears.length


    val NonLeapYearsInCycle = YearsInCycle - LeapYearsInCycle


    val MonthsInNonLeapYear = 12


    val MonthsInLeapYear = MonthsInNonLeapYear+1


    def cycle(number: Int): Int = (number / YearsInCycle) + 1


    def numberInCycle(number: Int): Int = number % YearsInCycle


    // TODO require(0 < _ < YearsInCycle)


    def isLeapYearInCycle(numberInCycle: Int) = LeapYears.contains(numberInCycle)


    def lengthInMonth(numberInCycle: Int) = if (isLeapYearInCycle(numberInCycle)) Year.MonthsInLeapYear else Year.MonthsInNonLeapYear


    def leapYearsBeforeInCycle(numberInCycle: Int): Int = LeapYears.count(_ < numberInCycle)


    def nonLeapYearsBeforeInCycle(numberInCycle: Int): Int = numberInCycle - 1 - leapYearsBeforeInCycle(numberInCycle)


    def monthsBeforeYearInCycle(numberInCycle: Int): Int =
        leapYearsBeforeInCycle(numberInCycle)*MonthsInLeapYear +
        nonLeapYearsBeforeInCycle(numberInCycle)*MonthsInNonLeapYear


    val MonthsBeforeYearInCycle = (1 to (YearsInCycle+1)) map (monthsBeforeYearInCycle(_))


    val MonthsInCycle = MonthsBeforeYearInCycle.last


    def yearMonthIsInCycle(number: Int): Int = MonthsBeforeYearInCycle.count(_ < number)


    private val Adu = List(1, 4, 6)


    private def isAdu(day: Day) = Adu.contains(day.dayOfWeek)


    object Kind extends Enumeration {

        type Type = Value


        val Short, Regular, Full = Value 
    }


    def apply(number: Int): Year = new Year(number)


    def apply(cycle: Int, numberInCycle: Int): Year = Year((cycle-1)*YearsInCycle + numberInCycle)
}
