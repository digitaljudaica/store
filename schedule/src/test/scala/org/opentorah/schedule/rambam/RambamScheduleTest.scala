package org.opentorah.schedule.rambam

import org.opentorah.calendar.jewish.Jewish.Year
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

final class RambamScheduleTest extends AnyFlatSpec, Matchers:

  "Rambam schedule" should "work" in {
    val result = RambamSchedule.scheduleYear(Year(5777), Formatter.narrow)
  }
