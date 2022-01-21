# Calendar #

Understanding the ignored for fun and schar.

Paper on Rambam's Laws of the Sanctification of the available at opentorah.org.

Code for working with Jewish calendar, both arithmetic and astronomical.
Makes possible calculations with dates, time intervals and angles exactly as they are
described by Rambam.

The code is written in Scala and uses extendable family polymorphism encoding with family members in
separate files inspired by a Stackoverflow
[post](https://stackoverflow.com/questions/1154571/scala-abstract-types-vs-generics/10891994#10891994)
by [ayvango](https://stackoverflow.com/users/837133/ayvango).

Note: 
- family polymorphism was introduced in the code in 2014;
- implementation with family members in separate files was developed in 2017-2018;
- it stopped working in June 2019 with the release of Scala 2.13.0;
- when the issue was not fixed by Scala 2.13.1 or 2.13.2, I filed a
  [bug](https://github.com/scala/bug/issues/11963) with the Scala team;
- general type projection is unsound and is going away in Scala 3 anyway;
- I was not able to figure out how to do split-file family polymorphism without it;
- so in January 2021 I gave up and went back to the "one trait to scope them all" approach...

The code:
- implements Rambam's calculations for arithmetic calendar (chapters 6-10);
- implements Rambam's calculations for astronomical calendar (chapters 11-19);
- has Rambam's numerical examples as unit tests;
- provides conversions between Jewish and secular (Gregorian) dates;
- supports [sanctification of the Sun](http://dub.podval.org/2019/07/18/sanctification-of-the-sun.html)
 calculations;
- generates Torah and Haftorah reading schedule for a number of customs;
- generates schedule of learning Rambam;

Enhancements being considered are:
- calculation of halachic times (zmanim)
  - raw data for the hazot calculations is obtainable from the [Naval Oceanography Portal](http://www.usno.navy.mil/USNO/astronomical-applications/data-services/rs-one-year-us);
    it is produced in accordance with the algorithm described in the Meese's book, I think...
  - there is [another source of data](http://www.timeanddate.com/worldclock/sunrise.html)
- integration with Google Calendar:
  - [iCalendar](http://en.wikipedia.org/wiki/ICalendar)
  - [iCalendar RFC](http://tools.ietf.org/html/rfc2445)
  - [Revised iCalendar RFC](http://tools.ietf.org/html/draft-ietf-calsify-rfc2445bis-08)
  - [iCal4j](http://ical4j.sourceforge.net/introduction.html)
  - [Publish web content events in iCalendar format](http://www.google.com/support/calendar/bin/answer.py?hl=en&amp;answer=48526)
  - [GData Java library](http://code.google.com/apis/gdata/client-java.html)


## Schedule ##

There are some schedule-related issues to look into:
- Magen Avraham 428:4 (6): When Pesach falls on Shabbat, so that residents of the Land of Israel
  read Shmini on the Shabbat following it, there are those that split Tazria and Metzora, and
  there are those that split Behar and Bechukotai, and if the year is leap, they split Matot and Masai;
- Baladi (Yemenite): instead of Matot and Masai combine Chukat and Balak;
- Daradaim (following Rabbi Saadia Gaon): instead of combining Matot and Masai, add to Korach Chukat
 to 20:21, and next Shabbos read the rest of Chukkat and Balak;
- 3 small aliyot (for 2/5/Shabbos day): look into and implement differences in customs;
- 3-year cycle: is anybody orthodox completing Torah cycle in three years? If yes, maybe we should generate that
 schedule too. There is a [list of Haftarot](https://faculty.biu.ac.il/~ofery/papers/haftarot3.pdf);

## Sources ##

### Torah readings ###

Shulchan Aruch Orach Chaim (and Mishna Brura):
425:1-2 (4, 8, 12), 428:4,6,8, 488:3; 490:1, 494:1-2; 559:4,
584:2 (8), 601:1, 621:1 (4), 622:2, 659 (4), 662:3 (4),
663:1,3 (4), 668:2, 669
Chanukah: 684 (3, 8, 14, 17),
693:4

TODO Rambam corresponding to Shulchan Aruch Orach Chayim 428


### Interesting sources that were not used ### 
https://en.wikipedia.org/wiki/Hebrew_calendar
https://en.wikibooks.org/wiki/Mathematics_of_the_Jewish_Calendar
https://orot.ac.il/sites/default/files/morashtenu/16-3.pdf
["Calendrical Calculations"](https://www.amazon.com/Calendrical-Calculations-Ultimate-Edward-Reingold/dp/1107683165)
by Edward M. Reingold and Nachum Dershowitz

### מקורות קריאת התורה למועדים

פסח: ג. שו"ע סי' תפח סע' ג
מוציאין שני ספרים וקורין בראשון חמשה גברי (<ג> ואם מוסיפין ביום טוב ע"ל ריש סימן רפ"ב)
  בפרשת בא מן משכו עד מארץ מצרים על צבאותם,
  (ט) ג ומפטיר קורא בשני בפרשת פנחס ובחודש הראשון ומפטיר ביהושע (י) בעת ההיא,
סי' תצ סע' א, ה-ו ומשנה ברורה
סי' תצד סע' א- ב ומשנה ברורה
סי' תקנט סע' ד ומשנה ברורה
סי' תקפד סע' ב ומשנה ברורה [בעיקר: סק"ח]
סי' תרא סע' א ומשנה ברורה
סי' תרכא סע' א ומשנה ברורה [בעיקר סק"ד]
סי' תרכב סע' ב ומשנה ברורה
סי' תרנט ומשנה ברורה [בעיקר: סק"ד]
סי' תרסב סע' ג ומשנה ברורה [בעיקר: סק"ד]
סי' תרסג סע' א, ג ומשנה ברורה [בעיקר: סק"ד]
סי' תרסח סע' ב ומשנה ברורה
סי' תרסט ומשנה ברורה
סי' תרפד ומשנה ברורה [בעיקר: ס"ק ג, ח, יד, יז]
סי' תרצג סע' ד ומשנה ברורה
סי' תכה סע' א- ב ומשנה ברורה [בעיקר: ס"ק ד, ח, יב]
סי' תכח סע' ד, ו, ח ומשנה ברורה


### Чтение в Хануку, ашкеназ и сфарад ###

שולחן ערוך אורח חיים הלכות חנוכה סימן תרפד
[א*] <א> קורין בקרבנות (א) הנשיאים שבפרשת נשא, שלשה בכל יום;
ומתחילין <ב> בברכת (ב) כהנים (וי"א שמתחילין ביום כלות משה) (טור),
(וכן אנו נוהגין), וקורא אותו (ג) עם כהן ולוי, וישראל קורא ביום הראשון.
(וי"א שהכהן קורא כל אותה הפרשה עד ביום הראשון, והלוי והישראל קורין ביום הראשון) (מנהגים) (וכן נוהגין);
ביום שני קורא כהן ביום השני עד פר אחד בן בקר, ולוי עד ביום השלישי, וישראל חוזר וקורא:
ביום השני, ועל דרך זה בכל יום. הגה: וי"א שישראל קורא ביום שלאחריו, דהיינו ביום הג'
, וכן בכל יום; וכן נוהגין (הגהות מיימוני פי"ח);
ביום שמיני מתחילין: ביום (ד) השמיני, וגומרין כל הסדר,
  וקורין פרשה ראשונה של בהעלותך (ונוהגים לסיים כן עשה את המנורה) (מנהגים
