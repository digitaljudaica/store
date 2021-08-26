package org.opentorah.calendar.service

import org.opentorah.calendar.{Calendar, YearsCycle}
import org.opentorah.calendar.jewish.{Jewish, LeapYearsCycle, Season, Shemittah, SpecialDay, Sun, YearType}
import org.opentorah.calendar.roman.Gregorian
import org.opentorah.html
import org.opentorah.metadata.{Language, LanguageSpec, Numbered, WithNames}
import org.opentorah.schedule.rambam.RambamSchedule
import org.opentorah.schedule.tanach.{Chitas, Schedule}
import org.opentorah.texts.rambam.{MishnehTorah, SeferHamitzvosLessons}
import org.opentorah.texts.tanach.{Custom, Haftarah, Psalms, Reading, Span, Torah}
import org.opentorah.util.Collections
import org.opentorah.xml.{PrettyPrinter, ScalaXml}

sealed abstract class Renderer(location: Location, spec: LanguageSpec) {
  protected val calendar: Calendar

  private final def getName(other: Boolean = false): String = if (!other) name else otherName

  protected def name: String

  protected def otherName: String

  private final def getYear(yearStr: String): calendar.Year = calendar.Year(yearStr.toInt)

  private final def getMonth(yearStr: String, monthStr: String): calendar.Month = {
    val year = getYear(yearStr)
    val monthName: Option[calendar.Month.Name] = calendar.Month.forName(monthStr)
    if (monthName.isDefined) year.month(monthName.get)
    else year.month(monthStr.toInt)
  }

  protected def jewish(day: Calendar#DayBase): Jewish.Day

  protected def gregorian(day: Calendar#DayBase): Gregorian.Day

  protected def first(day: Calendar#DayBase): Calendar#DayBase

  protected def second(day: Calendar#DayBase): Calendar#DayBase

  //  private def toLanguageString(what: LanguageString): String = what.toLanguageString(spec)

  private def yearUrl(year: Calendar#YearBase, other: Boolean = false): Seq[String] =
    Seq(getName(other), year.number.toString)

  private def monthUrl(month: Calendar#MonthBase, other: Boolean = false): Seq[String] =
    Seq(getName(other), month.year.number.toString, month.numberInYear.toString)

  private def monthNameUrl(month: Calendar#MonthBase, other: Boolean = false): Seq[String] =
    Seq(getName(other), month.year.number.toString, month.name.toLanguageString(spec))

  private def dayUrl(day: Calendar#DayBase, other: Boolean = false): Seq[String] =
    Seq(getName(other), day.year.number.toString, day.month.numberInYear.toString, day.numberInMonth.toString)

  private def yearLink(year: Calendar#YearBase, other: Boolean = false, text: Option[String] = None): ScalaXml.Element =
    navLink(yearUrl(year, other = other), text.getOrElse(year.toLanguageString(spec)))

  private def monthLink(month: Calendar#MonthBase): ScalaXml.Element =
    navLink(monthUrl(month), month.numberInYearToLanguageString(spec))

  private def monthNameLink(month: Calendar#MonthBase, other: Boolean = false, text: Option[String] = None): ScalaXml.Element =
    navLink(monthNameUrl(month, other = other), text.getOrElse(month.name.toLanguageString(spec)))

  private def dayLink(day: Calendar#DayBase, other: Boolean = false, text: Option[String] = None): ScalaXml.Element =
    navLink(dayUrl(day, other = other), text.getOrElse(day.numberInMonthToLanguageString(spec)))

  private def navLink(url: Seq[String], text: String): ScalaXml.Element =
    html.a(url).setQuery(suffix).addClass("nav")(text)

  private def suffix: String = Renderer.suffix(location, spec)

  private def dayLinks(day: Calendar#DayBase, other: Boolean): ScalaXml.Element =
    <span>
    {yearLink(day.year, other = other)}
    {monthNameLink(day.month, other = other)}
    {if (day.number > 1) dayLink(day-1, other = other, text = Some("<")) else <span>{Renderer.earlyGregorianMessage} </span>}
    {dayLink(day, other = other)}
    {dayLink(day+1, other = other, text = Some(">"))}
    </span>

  def renderLanding: String =
    renderHtml(Seq(name), dayLinks(Gregorian.now.to(calendar).day, other = false))

  def renderYear(yearStr: String): String = {
    val year: Calendar#YearBase = getYear(yearStr)
    renderHtml(
      yearUrl(year),
      <div>
        {yearLink(year-1, text = Some("<"))}
        {yearLink(year)}
        {yearLink(year+1, text = Some(">"))}
        <table>
          <tbody>
            {year.months.map { (month: Calendar#MonthBase) =>
            <tr>
              <td>{monthLink(month)}</td>
              <td>{monthNameLink(month)}</td>
            </tr>
          }}
          </tbody>
        </table>
        {renderYearInformation(year)}
      </div>
    )
  }

  protected def renderYearInformation(year: Calendar#YearBase): Seq[ScalaXml.Element] = Seq.empty

  def renderMonth(yearStr: String, monthStr: String): String = {
    val month: Calendar#MonthBase = getMonth(yearStr, monthStr)
    renderHtml(monthNameUrl(month),
      <div>
      {yearLink(month.year)}
      {monthNameLink(month-1, text = Some("<"))}
      {monthNameLink(month)}
      {monthNameLink(month+1, text = Some(">"))}
      <table>
        <tbody>
        {month.days.map { (day: Calendar#DayBase) => <tr><td>{dayLink(day)}</td></tr>}}
        </tbody>
      </table>
      </div>
    )
  }

  def renderDay(yearStr: String, monthStr: String, dayStr: String): String = {
    val day: Calendar#DayBase = getMonth(yearStr, monthStr).day(dayStr.toInt)
    val jewishDay: Jewish.Day = jewish(day)
    val firstDay: Calendar#DayBase = first(day)
    val secondDay: Calendar#DayBase = second(day)

    val daySchedule = Schedule.get(jewishDay, inHolyLand = location.inHolyLand)

    renderHtml(dayUrl(firstDay),
      <div>
      <div>{dayLinks(firstDay, other = false)} {firstDay.name.toLanguageString(spec)}</div>
      <div>{dayLinks(secondDay, other = true)} {secondDay.name.toLanguageString(spec)}</div>
      <div>{daySchedule.dayNames.map { (withNames: WithNames) =>
        <span class="name">{withNames.names.doFind(spec).name}</span>}}</div>
      {renderOptionalReading("Morning", daySchedule.morning)}
      {renderOptionalReading("Purim morning alternative", daySchedule.purimAlternativeMorning)}
      {if (!jewishDay.isShabbosMevarchim) Seq.empty[ScalaXml.Element] else renderShabbosMevarchim(jewishDay.month.next)}
      <span class="heading">Chitas</span>
      {renderChitas(daySchedule.chitas)}
      <span class="heading">Tehillim</span>
      {renderTehillim(jewishDay)}
      <div class="heading">Rambam</div>
      {renderRambam(RambamSchedule.forDay(jewishDay))}
      {renderOptionalReading("Afternoon", daySchedule.afternoon)}
      </div>
    )
  }

  private def renderShabbosMevarchim(month: Jewish.Month): Seq[ScalaXml.Element] = Seq(
    <span class="subheading">Shabbos Mevarchim</span>,
    <table><tr>
      <td>{month.name.toLanguageString(spec)}</td>
      {if (month.prev.length == 30) <td>{month.prev.lastDay.name.toLanguageString(spec)}</td> else <td/>}
      <td>{month.firstDay.name.toLanguageString(spec)}</td>
      <td>{month.newMoon.toLanguageString(spec)}</td>
    </tr></table>
  )

  private def renderChitas(chitas: Chitas): ScalaXml.Element = {
    def renderFragment(fragment: Torah.Fragment): Seq[ScalaXml.Element] = Seq(
      <td><span>{fragment.toLanguageString(spec)}</span></td>,
      <td>{renderSource(fragment.source)}</td>
    )

    <table><tbody>
      <tr>{renderFragment(chitas.first)}</tr> +:
      {chitas.second.fold(Seq.empty[ScalaXml.Element])(fragment => Seq(<tr>{renderFragment(fragment)}</tr>))}
    </tbody></table>
  }

  private def renderTehillim(day: Jewish.Day): ScalaXml.Element = {
    val forDayOfMonth: Span =
      if ((day.numberInMonth == 29) && day.month.length == 29) Span(Psalms.days(29-1).from, Psalms.days(30-1).to)
      else Psalms.days(day.numberInMonth-1)

    val forDayOfWeek: Span =
      Psalms.weekDays(day.numberInWeek-1)

    <table>
      <tr><td>for day of month</td><td><span>{forDayOfMonth.toLanguageString(spec)}</span></td></tr>
      <tr><td>for day of week</td><td><span>{forDayOfWeek.toLanguageString(spec)}</span></td></tr>
    </table>
  }

  private def renderRambam(schedule: RambamSchedule): Seq[ScalaXml.Element] = Seq(
    <span class="subheading">3 chapters</span>,
    <table>
      <tr><td>Cycle</td><td>Lesson</td></tr>
      <tr>
        <td>{spec.toString(schedule.threeChapters.cycle)}</td>
        <td>{spec.toString(schedule.threeChapters.lesson)}</td>
      </tr>
    </table>,
    <span class="subheading">chapters</span>,
    <table>
      <tr><td/><td>Book</td><td>Part</td><td>Chapter</td></tr>
      <tr><td>{spec.toString(1)}</td>{renderRambamChapter(schedule.threeChapters.chapter1)}</tr>
      <tr><td>{spec.toString(2)}</td>{renderRambamChapter(schedule.threeChapters.chapter2)}</tr>
      <tr><td>{spec.toString(3)}</td>{renderRambamChapter(schedule.threeChapters.chapter3)}</tr>
    </table>,
    <span class="subheading">Sefer Hamitzvos</span>,
    <table>{schedule.seferHamitzvos.parts.map { (part: SeferHamitzvosLessons.Part) =>
      <tr><td>{part.toLanguageString(spec)}</td></tr>
    }}</table>,
    <span class="subheading">1 chapter</span>,
    <table>
      <tr><td>Cycle</td><td>Year</td><td>Chapter number</td></tr>
      <tr>
        <td>{spec.toString(schedule.oneChapter.cycle)}</td>
        <td>{spec.toString(schedule.oneChapter.year)}</td>
        <td>{spec.toString(schedule.oneChapter.chapterNumber)}</td>
      </tr>
    </table>,
    <span class="subheading">chapter</span>,
    <table>
      <tr><td>Book</td><td>Part</td><td>Chapter</td></tr>
      <tr>{renderRambamChapter(schedule.oneChapter.chapter)}</tr>
    </table>
  )

  private def renderRambamChapter(chapter: MishnehTorah.Chapter): Seq[ScalaXml.Element] = Seq(
    <td>{chapter.part.book.toLanguageString(spec)}</td>,
    <td>{chapter.part.toLanguageString(spec)}</td>,
    <td>{chapter.toLanguageString(spec)}</td>
  )

  private def renderOptionalReading(name: String, reading: Option[Reading]): Seq[ScalaXml.Element] = {
    reading.fold(Seq.empty[ScalaXml.Element]) { reading => Seq(<div>
      <span class="heading">{name}</span>
      {
        val maftirCommonOnly = reading.maftir.commonOnly
        val haftarahCommonOnly = reading.haftarah.commonOnly
        val noMaftirHaftarah: Boolean =
          maftirCommonOnly.fold(false)(_.isEmpty) && haftarahCommonOnly.fold(false)(_.isEmpty)
        val varyingMaftirAndHaftarah: Boolean = maftirCommonOnly.isEmpty && haftarahCommonOnly.isEmpty

        <div>
          {renderCustoms("Torah", reading.torah, renderTorah)}
          {if (noMaftirHaftarah) Seq.empty[ScalaXml.Element] else
          if (varyingMaftirAndHaftarah)
            renderCustoms("Maftir and Haftarah", reading.maftirAndHaftarah, renderMaftirAndHaftarah)
          else
            renderCustoms("Maftir", reading.maftir, renderMaftir) ++
            renderCustoms("Haftarah", reading.haftarah, renderHaftarah)
          }
        </div>
      }
    </div>)}
  }

  private def renderTorah(torah: Torah): Seq[ScalaXml.Element] =
    torah.spans.zipWithIndex map { case (fragment, index) =>
      <tr>
        <td>{spec.toString(index + 1)}</td>
        <td>{fragment.toLanguageString(spec)}</td>
        <td>{renderSource(fragment.source)}</td>
      </tr>
    }

  private def renderMaftir(maftir: Option[Torah.Maftir]): Seq[ScalaXml.Element] =
    Seq(maftir.fold(<tr><td>None</td></tr>)(maftir =>
      <tr>
        <td>{maftir.toLanguageString(spec)}</td>
        <td>{renderSource(maftir.source)}</td>
      </tr>
    ))

  private def renderHaftarah(haftarah: Option[Haftarah]): Seq[ScalaXml.Element] =
    haftarah.fold(Seq(<tr><td>None</td></tr>)){ haftarah =>
      val spans = haftarah.spans
      val parts: Seq[Seq[Haftarah.BookSpan]] =
        Collections.group[Haftarah.BookSpan, Option[WithNames]](spans, span => span.source)
      parts map { (part: Seq[Haftarah.BookSpan]) =>
        <tr>
          <td>{Haftarah.toLanguageString(part)(spec)}</td>
          <td>{renderSource(part.head.source)}</td>
        </tr>
      }}

  private def renderMaftirAndHaftarah(maftirAndHaftarah: Option[Reading.MaftirAndHaftarah]): Seq[ScalaXml.Element] =
    renderMaftir(maftirAndHaftarah.flatMap(_.maftir)) ++
      renderHaftarah(maftirAndHaftarah.map(_.haftarah))

  private def renderSource(source: Option[WithNames]): String =
    source.fold[String]("")(_.toLanguageString(spec))

  private def renderCustoms[T](
    what: String,
    customs: Custom.Of[T],
    renderer: T => Seq[ScalaXml.Element]
  ): Seq[ScalaXml.Element] =
    <span class="subheading">{what}</span> +:
    customs.customs.toSeq.map { case (custom: Custom, valueForCustom /*: T*/) =>
      <table class="custom">
        <caption>{custom.toLanguageString(spec)}</caption>
        <tbody>{renderer(valueForCustom)}</tbody>
      </table>
    }

  private def renderHtml(url: Seq[String], content: ScalaXml.Element): String =
    Renderer.renderHtml(url, content, location, spec)
}

object Renderer {
  private val jewishRendererName: String = "jewish"

  private val gregorianRenderername: String = "gregorian"

  private val earlyGregorianMessage: String = "Gregorian dates before year 1 are not supported!"

  private final class JewishRenderer(location: Location, spec: LanguageSpec) extends Renderer(location, spec) {
    override protected val calendar: Calendar = Jewish

    override protected def name: String = jewishRendererName

    override protected def otherName: String = gregorianRenderername

    override protected def jewish(day: Calendar#DayBase): Jewish.Day = day.asInstanceOf[Jewish.Day]

    override protected def gregorian(day: Calendar#DayBase): Gregorian.Day = {
      try {
        Gregorian.Day.from(jewish(day).asInstanceOf[Calendar#Day])
      } catch {
        case _: IllegalArgumentException => Gregorian.Year(1).month(1).day(1)
      }
    }

    override protected def first(day: Calendar#DayBase): Calendar#DayBase = jewish(day)

    override protected def second(day: Calendar#DayBase): Calendar#DayBase = gregorian(day)

    override protected def renderYearInformation(yearRaw: Calendar#YearBase): Seq[ScalaXml.Element] = {
      val year: Jewish.Year = yearRaw.asInstanceOf[Jewish.Year]
      val delay = year.newYearDelay

      val numbers: ScalaXml.Element =
        <table>
          <tr><td>from creation</td><td>{year.toLanguageString(spec)}</td></tr>
          <tr><td>is leap</td><td>{year.isLeap.toString}</td></tr>
          <tr><td>months</td><td>{spec.toString(year.lengthInMonths)}</td></tr>
          <tr><td>days</td><td>{spec.toString(year.lengthInDays)}</td></tr>
          <tr><td>type</td><td>{YearType.forYear(year).toString}</td></tr>
          <tr><td>Molad</td><td>{year.newMoon.toLanguageString(spec)}</td></tr>
          <tr><td>New Year Delay</td><td>{s"$delay (${delay.days})"}</td></tr>
        </table>

      def cycle(name: String, yearsCycle: YearsCycle): ScalaXml.Element = {
        val in = yearsCycle.forYear(year)
        <tr>
          <td>{name}</td>
          <td>{spec.toString(in.cycleNumber)}</td>
          <td>{spec.toString(in.numberInCycle)}</td>
        </tr>
      }

      val cycles: ScalaXml.Element =
        <table>
          <tr><td>Cycle"</td><td>Number</td><td>In Cycle"</td></tr>
          {cycle("Leap Years", LeapYearsCycle)}
          {cycle("Shemittah", Shemittah)}
          {cycle("Birchas Hachamo", Sun.Shmuel)}
        </table>

      val tkufot: ScalaXml.Element = {
        def tkufa(flavor: Season.ForYear, season: Season): String =
          flavor.seasonForYear(season, year).toLanguageString(spec)

        <table>
          <tr><td>Tkufa</td><td>Shmuel</td><td>Rav Ada"</td></tr>
          {Season.values.map { season =>
          <tr>
            <td>{season.toLanguageString(spec)}</td>
            <td>{tkufa(Sun.Shmuel, season)}</td>
            <td>{tkufa(Sun.RavAda, season)}</td>
          </tr>}}
        </table>
      }

      val festivalDays: Seq[(SpecialDay, Jewish.Day)] =
        SpecialDay.daysWithSpecialReadings(location == Location.HolyLand)
          .map(specialDay => specialDay -> specialDay.correctedDate(year))
          .toSeq.sortBy(_._2)(Numbered.numberedOrdering[Jewish.Day])

      val festivals: ScalaXml.Element =
        <table>
        {festivalDays.map { case (specialDay, day) =>
        <tr>
          <td>{specialDay.toLanguageString(spec)}</td>
          <td>{day.toLanguageString(spec)}</td>
          <td>{day.name.toLanguageString(spec)}</td>
        </tr>}}
      </table>

      Seq(
        <span class="heading">Year</span>,
        numbers,
        <span class="heading">Cycles</span>,
        cycles,
        <span class="heading">Tkufot</span>,
        tkufot,
        <span class="heading">Special Days</span>,
        festivals
      )
    }
  }

  private final class GregorianRenderer(location: Location, spec: LanguageSpec) extends Renderer(location, spec) {
    override protected val calendar: Calendar = Gregorian

    override protected def name: String = gregorianRenderername

    override protected def otherName: String = jewishRendererName

    override protected def jewish(day: Calendar#DayBase): Jewish.Day = gregorian(day).to(Jewish)

    override protected def gregorian(day: Calendar#DayBase): Gregorian.Day = day.asInstanceOf[Gregorian.Day]

    override protected def first(day: Calendar#DayBase): Calendar#DayBase = gregorian(day)

    override protected def second(day: Calendar#DayBase): Calendar#DayBase = jewish(day)
  }

  def renderer(kindStr: String, location: Location, spec: LanguageSpec): Renderer = {
    if (kindStr == jewishRendererName) new JewishRenderer(location, spec)
    else if (kindStr == gregorianRenderername) new GregorianRenderer(location, spec)
    else throw new IllegalArgumentException(s"Unrecognized kind $kindStr")
  }

  def renderRoot(location: Location, spec: LanguageSpec): String = renderHtml(
    url = Seq.empty,
    content =
      <div>
        <div>{html.a(Seq(jewishRendererName))(text = "jewish")}</div>,
        <div>{html.a(Seq(gregorianRenderername))(text = "gregorian")}</div>
      </div>,
    location = location,
    spec = spec
  )

  def renderHtml(
    url: Seq[String],
    content: ScalaXml.Element,
    location: Location,
    spec: LanguageSpec
  ): String = {
    val languages: Seq[ScalaXml.Element] = Language.values.map(_.toSpec) map { spec1 =>
      val languageName = spec1.languageName
      if (spec1.language == spec.language) <span class="picker">{languageName}</span>
      else html.a(url).setQuery(suffix(location, spec1)).addClass("picker")(text = languageName)
    }

    val locations: Seq[ScalaXml.Element] = Seq(Location.HolyLand, Location.Diaspora).map { location1 =>
      if (location1 == location) <span class="picker">{location1.name}</span>
      else html.a(url).setQuery(suffix(location1, spec)).addClass("picker")(text = location1.name)
    }

    //        title("Reading Schedule")?
    val result =
      <html dir={direction(spec)}>
        <head>
          <link rel="stylesheet" type="text/css" href="/style.css"/>
        </head>
        <body>
          {languages}
          {locations}
          {content}
        </body>
      </html>

    PrettyPrinter.default.render(ScalaXml)(result)
  }

  private def direction(spec: LanguageSpec): String =
    if (spec.language.contains(Language.Hebrew)) "rtl" else "ltr"

  private def suffix(location: Location, spec: LanguageSpec): String =
    s"inHolyLand=${location.inHolyLand}&lang=${spec.languageName}"
}
