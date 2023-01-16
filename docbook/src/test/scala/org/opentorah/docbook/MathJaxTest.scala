package org.opentorah.docbook

import org.opentorah.fop.Svg
import org.opentorah.math.MathJaxRunner
import org.opentorah.xml.{ScalaXml, XLink}
import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers

class MathJaxTest extends AnyFlatSpecLike, Matchers:

  private def test(
    name: String,
    useMathJaxV3: Boolean,
    tex: String,
    svgExpected: String
  ): Unit =
    val project = PluginTestProject(
      prefix = "mathJaxTestProjects",
      name = s"$name-useMathJaxV3-$useMathJaxV3",
      document = DocBook.prettyPrinter.renderWithHeader(ScalaXml)(
        <article xmlns={DocBook.namespace.uri} version={DocBook.version}>
          <para>{s"$$$tex$$"}</para>
        </article>
      ),
      isPdfEnabled = true,
      isMathJaxEnabled = true,
      useMathJaxV3 = useMathJaxV3
    )

    val log: String = project.run(logInfo = true)
    substring(log, MathJaxRunner.logStart, MathJaxRunner.logSep) shouldBe tex
    substring(log, MathJaxRunner.logSep, MathJaxRunner.logEnd) shouldBe svgExpected


  private def substring(string: String, from: String, to: String): String =
    val fromIndex = string.indexOf(from)
    if fromIndex == -1 then throw IllegalAccessException(s"no '$from' in $string")
    val result: String = string.substring(fromIndex + from.length)
    val toIndex = result.indexOf(to)
    if toIndex == -1 then throw IllegalAccessException(s"no '$to' in $result")
    result.substring(0, toIndex)

  it should "typeset E = mc^2 using MathJax 2" in test("emc2", useMathJaxV3 = false, "E = mc^2",
     s"""|<svg xmlns:xlink="${XLink.namespace.uri}" width="9.155ex" height="3.009ex" style="vertical-align: -0.505ex;" viewBox="0 -1078.4 3941.5 1295.7" role="img" focusable="false" xmlns="${Svg.namespace.uri}">
         |<defs>
         |<path stroke-width="1" id="E1-MJMATHI-45" d="M492 213Q472 213 472 226Q472 230 477 250T482 285Q482 316 461 323T364 330H312Q311 328 277 192T243 52Q243 48 254 48T334 46Q428 46 458 48T518 61Q567 77 599 117T670 248Q680 270 683 272Q690 274 698 274Q718 274 718 261Q613 7 608 2Q605 0 322 0H133Q31 0 31 11Q31 13 34 25Q38 41 42 43T65 46Q92 46 125 49Q139 52 144 61Q146 66 215 342T285 622Q285 629 281 629Q273 632 228 634H197Q191 640 191 642T193 659Q197 676 203 680H757Q764 676 764 669Q764 664 751 557T737 447Q735 440 717 440H705Q698 445 698 453L701 476Q704 500 704 528Q704 558 697 578T678 609T643 625T596 632T532 634H485Q397 633 392 631Q388 629 386 622Q385 619 355 499T324 377Q347 376 372 376H398Q464 376 489 391T534 472Q538 488 540 490T557 493Q562 493 565 493T570 492T572 491T574 487T577 483L544 351Q511 218 508 216Q505 213 492 213Z"></path>
         |<path stroke-width="1" id="E1-MJMAIN-3D" d="M56 347Q56 360 70 367H707Q722 359 722 347Q722 336 708 328L390 327H72Q56 332 56 347ZM56 153Q56 168 72 173H708Q722 163 722 153Q722 140 707 133H70Q56 140 56 153Z"></path>
         |<path stroke-width="1" id="E1-MJMATHI-6D" d="M21 287Q22 293 24 303T36 341T56 388T88 425T132 442T175 435T205 417T221 395T229 376L231 369Q231 367 232 367L243 378Q303 442 384 442Q401 442 415 440T441 433T460 423T475 411T485 398T493 385T497 373T500 364T502 357L510 367Q573 442 659 442Q713 442 746 415T780 336Q780 285 742 178T704 50Q705 36 709 31T724 26Q752 26 776 56T815 138Q818 149 821 151T837 153Q857 153 857 145Q857 144 853 130Q845 101 831 73T785 17T716 -10Q669 -10 648 17T627 73Q627 92 663 193T700 345Q700 404 656 404H651Q565 404 506 303L499 291L466 157Q433 26 428 16Q415 -11 385 -11Q372 -11 364 -4T353 8T350 18Q350 29 384 161L420 307Q423 322 423 345Q423 404 379 404H374Q288 404 229 303L222 291L189 157Q156 26 151 16Q138 -11 108 -11Q95 -11 87 -5T76 7T74 17Q74 30 112 181Q151 335 151 342Q154 357 154 369Q154 405 129 405Q107 405 92 377T69 316T57 280Q55 278 41 278H27Q21 284 21 287Z"></path>
         |<path stroke-width="1" id="E1-MJMATHI-63" d="M34 159Q34 268 120 355T306 442Q362 442 394 418T427 355Q427 326 408 306T360 285Q341 285 330 295T319 325T330 359T352 380T366 386H367Q367 388 361 392T340 400T306 404Q276 404 249 390Q228 381 206 359Q162 315 142 235T121 119Q121 73 147 50Q169 26 205 26H209Q321 26 394 111Q403 121 406 121Q410 121 419 112T429 98T420 83T391 55T346 25T282 0T202 -11Q127 -11 81 37T34 159Z"></path>
         |<path stroke-width="1" id="E1-MJMAIN-32" d="M109 429Q82 429 66 447T50 491Q50 562 103 614T235 666Q326 666 387 610T449 465Q449 422 429 383T381 315T301 241Q265 210 201 149L142 93L218 92Q375 92 385 97Q392 99 409 186V189H449V186Q448 183 436 95T421 3V0H50V19V31Q50 38 56 46T86 81Q115 113 136 137Q145 147 170 174T204 211T233 244T261 278T284 308T305 340T320 369T333 401T340 431T343 464Q343 527 309 573T212 619Q179 619 154 602T119 569T109 550Q109 549 114 549Q132 549 151 535T170 489Q170 464 154 447T109 429Z"></path>
         |</defs>
         |<g stroke="currentColor" fill="currentColor" stroke-width="0" transform="matrix(1 0 0 -1 0 0)">
         | <use xlink:href="#E1-MJMATHI-45" x="0" y="0"></use>
         | <use xlink:href="#E1-MJMAIN-3D" x="1042" y="0"></use>
         | <use xlink:href="#E1-MJMATHI-6D" x="2098" y="0"></use>
         |<g transform="translate(2977,0)">
         | <use xlink:href="#E1-MJMATHI-63" x="0" y="0"></use>
         | <use transform="scale(0.861)" xlink:href="#E1-MJMAIN-32" x="503" y="421"></use>
         |</g>
         |</g>
         |</svg>""".stripMargin
  )

  it should "typeset E = mc^2 using MathJax 3" in test("emc2", useMathJaxV3 = true, "E = mc^2",
    s"""|<svg style="vertical-align: -0.186ex;" xmlns="http://www.w3.org/2000/svg" width="8.699ex" height="2.072ex" role="img" focusable="false" viewBox="0 -834 3845.2 916" xmlns:xlink="http://www.w3.org/1999/xlink"><defs><path id="MJX-1-TEX-I-1D438" d="M492 213Q472 213 472 226Q472 230 477 250T482 285Q482 316 461 323T364 330H312Q311 328 277 192T243 52Q243 48 254 48T334 46Q428 46 458 48T518 61Q567 77 599 117T670 248Q680 270 683 272Q690 274 698 274Q718 274 718 261Q613 7 608 2Q605 0 322 0H133Q31 0 31 11Q31 13 34 25Q38 41 42 43T65 46Q92 46 125 49Q139 52 144 61Q146 66 215 342T285 622Q285 629 281 629Q273 632 228 634H197Q191 640 191 642T193 659Q197 676 203 680H757Q764 676 764 669Q764 664 751 557T737 447Q735 440 717 440H705Q698 445 698 453L701 476Q704 500 704 528Q704 558 697 578T678 609T643 625T596 632T532 634H485Q397 633 392 631Q388 629 386 622Q385 619 355 499T324 377Q347 376 372 376H398Q464 376 489 391T534 472Q538 488 540 490T557 493Q562 493 565 493T570 492T572 491T574 487T577 483L544 351Q511 218 508 216Q505 213 492 213Z"></path><path id="MJX-1-TEX-N-3D" d="M56 347Q56 360 70 367H707Q722 359 722 347Q722 336 708 328L390 327H72Q56 332 56 347ZM56 153Q56 168 72 173H708Q722 163 722 153Q722 140 707 133H70Q56 140 56 153Z"></path><path id="MJX-1-TEX-I-1D45A" d="M21 287Q22 293 24 303T36 341T56 388T88 425T132 442T175 435T205 417T221 395T229 376L231 369Q231 367 232 367L243 378Q303 442 384 442Q401 442 415 440T441 433T460 423T475 411T485 398T493 385T497 373T500 364T502 357L510 367Q573 442 659 442Q713 442 746 415T780 336Q780 285 742 178T704 50Q705 36 709 31T724 26Q752 26 776 56T815 138Q818 149 821 151T837 153Q857 153 857 145Q857 144 853 130Q845 101 831 73T785 17T716 -10Q669 -10 648 17T627 73Q627 92 663 193T700 345Q700 404 656 404H651Q565 404 506 303L499 291L466 157Q433 26 428 16Q415 -11 385 -11Q372 -11 364 -4T353 8T350 18Q350 29 384 161L420 307Q423 322 423 345Q423 404 379 404H374Q288 404 229 303L222 291L189 157Q156 26 151 16Q138 -11 108 -11Q95 -11 87 -5T76 7T74 17Q74 30 112 181Q151 335 151 342Q154 357 154 369Q154 405 129 405Q107 405 92 377T69 316T57 280Q55 278 41 278H27Q21 284 21 287Z"></path><path id="MJX-1-TEX-I-1D450" d="M34 159Q34 268 120 355T306 442Q362 442 394 418T427 355Q427 326 408 306T360 285Q341 285 330 295T319 325T330 359T352 380T366 386H367Q367 388 361 392T340 400T306 404Q276 404 249 390Q228 381 206 359Q162 315 142 235T121 119Q121 73 147 50Q169 26 205 26H209Q321 26 394 111Q403 121 406 121Q410 121 419 112T429 98T420 83T391 55T346 25T282 0T202 -11Q127 -11 81 37T34 159Z"></path><path id="MJX-1-TEX-N-32" d="M109 429Q82 429 66 447T50 491Q50 562 103 614T235 666Q326 666 387 610T449 465Q449 422 429 383T381 315T301 241Q265 210 201 149L142 93L218 92Q375 92 385 97Q392 99 409 186V189H449V186Q448 183 436 95T421 3V0H50V19V31Q50 38 56 46T86 81Q115 113 136 137Q145 147 170 174T204 211T233 244T261 278T284 308T305 340T320 369T333 401T340 431T343 464Q343 527 309 573T212 619Q179 619 154 602T119 569T109 550Q109 549 114 549Q132 549 151 535T170 489Q170 464 154 447T109 429Z"></path></defs><g stroke="currentColor" fill="currentColor" stroke-width="0" transform="scale(1,-1)"><g data-mml-node="math"><g data-mml-node="mi"><use data-c="1D438" xlink:href="#MJX-1-TEX-I-1D438"></use></g><g data-mml-node="mo" transform="translate(1041.8,0)"><use data-c="3D" xlink:href="#MJX-1-TEX-N-3D"></use></g><g data-mml-node="mi" transform="translate(2097.6,0)"><use data-c="1D45A" xlink:href="#MJX-1-TEX-I-1D45A"></use></g><g data-mml-node="msup" transform="translate(2975.6,0)"><g data-mml-node="mi"><use data-c="1D450" xlink:href="#MJX-1-TEX-I-1D450"></use></g><g data-mml-node="mn" transform="translate(466,363) scale(0.707)"><use data-c="32" xlink:href="#MJX-1-TEX-N-32"></use></g></g></g></g></svg>""".stripMargin
  )

  it should "typeset quadratic using MathJax 2" in test("quadratic", useMathJaxV3 = false, "x = {-b \\pm \\sqrt{b^2-4ac} \\over 2a}.",
     s"""|<svg xmlns:xlink="${XLink.namespace.uri}" width="18.429ex" height="7.009ex" style="vertical-align: -1.755ex;" viewBox="0 -2262.4 7934.7 3017.9" role="img" focusable="false" xmlns="${Svg.namespace.uri}">
         |<defs>
         |<path stroke-width="1" id="E1-MJMATHI-78" d="M52 289Q59 331 106 386T222 442Q257 442 286 424T329 379Q371 442 430 442Q467 442 494 420T522 361Q522 332 508 314T481 292T458 288Q439 288 427 299T415 328Q415 374 465 391Q454 404 425 404Q412 404 406 402Q368 386 350 336Q290 115 290 78Q290 50 306 38T341 26Q378 26 414 59T463 140Q466 150 469 151T485 153H489Q504 153 504 145Q504 144 502 134Q486 77 440 33T333 -11Q263 -11 227 52Q186 -10 133 -10H127Q78 -10 57 16T35 71Q35 103 54 123T99 143Q142 143 142 101Q142 81 130 66T107 46T94 41L91 40Q91 39 97 36T113 29T132 26Q168 26 194 71Q203 87 217 139T245 247T261 313Q266 340 266 352Q266 380 251 392T217 404Q177 404 142 372T93 290Q91 281 88 280T72 278H58Q52 284 52 289Z"></path>
         |<path stroke-width="1" id="E1-MJMAIN-3D" d="M56 347Q56 360 70 367H707Q722 359 722 347Q722 336 708 328L390 327H72Q56 332 56 347ZM56 153Q56 168 72 173H708Q722 163 722 153Q722 140 707 133H70Q56 140 56 153Z"></path>
         |<path stroke-width="1" id="E1-MJMAIN-2212" d="M84 237T84 250T98 270H679Q694 262 694 250T679 230H98Q84 237 84 250Z"></path>
         |<path stroke-width="1" id="E1-MJMATHI-62" d="M73 647Q73 657 77 670T89 683Q90 683 161 688T234 694Q246 694 246 685T212 542Q204 508 195 472T180 418L176 399Q176 396 182 402Q231 442 283 442Q345 442 383 396T422 280Q422 169 343 79T173 -11Q123 -11 82 27T40 150V159Q40 180 48 217T97 414Q147 611 147 623T109 637Q104 637 101 637H96Q86 637 83 637T76 640T73 647ZM336 325V331Q336 405 275 405Q258 405 240 397T207 376T181 352T163 330L157 322L136 236Q114 150 114 114Q114 66 138 42Q154 26 178 26Q211 26 245 58Q270 81 285 114T318 219Q336 291 336 325Z"></path>
         |<path stroke-width="1" id="E1-MJMAIN-B1" d="M56 320T56 333T70 353H369V502Q369 651 371 655Q376 666 388 666Q402 666 405 654T409 596V500V353H707Q722 345 722 333Q722 320 707 313H409V40H707Q722 32 722 20T707 0H70Q56 7 56 20T70 40H369V313H70Q56 320 56 333Z"></path>
         |<path stroke-width="1" id="E1-MJMAIN-32" d="M109 429Q82 429 66 447T50 491Q50 562 103 614T235 666Q326 666 387 610T449 465Q449 422 429 383T381 315T301 241Q265 210 201 149L142 93L218 92Q375 92 385 97Q392 99 409 186V189H449V186Q448 183 436 95T421 3V0H50V19V31Q50 38 56 46T86 81Q115 113 136 137Q145 147 170 174T204 211T233 244T261 278T284 308T305 340T320 369T333 401T340 431T343 464Q343 527 309 573T212 619Q179 619 154 602T119 569T109 550Q109 549 114 549Q132 549 151 535T170 489Q170 464 154 447T109 429Z"></path>
         |<path stroke-width="1" id="E1-MJMAIN-34" d="M462 0Q444 3 333 3Q217 3 199 0H190V46H221Q241 46 248 46T265 48T279 53T286 61Q287 63 287 115V165H28V211L179 442Q332 674 334 675Q336 677 355 677H373L379 671V211H471V165H379V114Q379 73 379 66T385 54Q393 47 442 46H471V0H462ZM293 211V545L74 212L183 211H293Z"></path>
         |<path stroke-width="1" id="E1-MJMATHI-61" d="M33 157Q33 258 109 349T280 441Q331 441 370 392Q386 422 416 422Q429 422 439 414T449 394Q449 381 412 234T374 68Q374 43 381 35T402 26Q411 27 422 35Q443 55 463 131Q469 151 473 152Q475 153 483 153H487Q506 153 506 144Q506 138 501 117T481 63T449 13Q436 0 417 -8Q409 -10 393 -10Q359 -10 336 5T306 36L300 51Q299 52 296 50Q294 48 292 46Q233 -10 172 -10Q117 -10 75 30T33 157ZM351 328Q351 334 346 350T323 385T277 405Q242 405 210 374T160 293Q131 214 119 129Q119 126 119 118T118 106Q118 61 136 44T179 26Q217 26 254 59T298 110Q300 114 325 217T351 328Z"></path>
         |<path stroke-width="1" id="E1-MJMATHI-63" d="M34 159Q34 268 120 355T306 442Q362 442 394 418T427 355Q427 326 408 306T360 285Q341 285 330 295T319 325T330 359T352 380T366 386H367Q367 388 361 392T340 400T306 404Q276 404 249 390Q228 381 206 359Q162 315 142 235T121 119Q121 73 147 50Q169 26 205 26H209Q321 26 394 111Q403 121 406 121Q410 121 419 112T429 98T420 83T391 55T346 25T282 0T202 -11Q127 -11 81 37T34 159Z"></path>
         |<path stroke-width="1" id="E1-MJSZ2-221A" d="M1001 1150Q1017 1150 1020 1132Q1020 1127 741 244L460 -643Q453 -650 436 -650H424Q423 -647 423 -645T421 -640T419 -631T415 -617T408 -594T399 -560T385 -512T367 -448T343 -364T312 -259L203 119L138 41L111 67L212 188L264 248L472 -474L983 1140Q988 1150 1001 1150Z"></path>
         |<path stroke-width="1" id="E1-MJMAIN-2E" d="M78 60Q78 84 95 102T138 120Q162 120 180 104T199 61Q199 36 182 18T139 0T96 17T78 60Z"></path>
         |</defs>
         |<g stroke="currentColor" fill="currentColor" stroke-width="0" transform="matrix(1 0 0 -1 0 0)">
         | <use xlink:href="#E1-MJMATHI-78" x="0" y="0"></use>
         | <use xlink:href="#E1-MJMAIN-3D" x="850" y="0"></use>
         |<g transform="translate(1906,0)">
         |<g transform="translate(120,0)">
         |<rect stroke="none" width="5509" height="60" x="0" y="220"></rect>
         |<g transform="translate(60,932)">
         | <use transform="scale(0.861)" xlink:href="#E1-MJMAIN-2212" x="0" y="0"></use>
         | <use transform="scale(0.861)" xlink:href="#E1-MJMATHI-62" x="778" y="0"></use>
         | <use transform="scale(0.861)" xlink:href="#E1-MJMAIN-B1" x="1208" y="0"></use>
         |<g transform="translate(1710,0)">
         | <use transform="scale(0.861)" xlink:href="#E1-MJSZ2-221A" x="0" y="162"></use>
         |<rect stroke="none" width="2817" height="51" x="861" y="1079"></rect>
         |<g transform="translate(861,0)">
         | <use transform="scale(0.861)" xlink:href="#E1-MJMATHI-62" x="0" y="0"></use>
         | <use transform="scale(0.861)" xlink:href="#E1-MJMAIN-32" x="429" y="308"></use>
         | <use transform="scale(0.861)" xlink:href="#E1-MJMAIN-2212" x="1030" y="0"></use>
         | <use transform="scale(0.861)" xlink:href="#E1-MJMAIN-34" x="1808" y="0"></use>
         | <use transform="scale(0.861)" xlink:href="#E1-MJMATHI-61" x="2309" y="0"></use>
         | <use transform="scale(0.861)" xlink:href="#E1-MJMATHI-63" x="2838" y="0"></use>
         |</g>
         |</g>
         |</g>
         |<g transform="translate(2311,-587)">
         | <use transform="scale(0.861)" xlink:href="#E1-MJMAIN-32" x="0" y="0"></use>
         | <use transform="scale(0.861)" xlink:href="#E1-MJMATHI-61" x="500" y="0"></use>
         |</g>
         |</g>
         |</g>
         | <use xlink:href="#E1-MJMAIN-2E" x="7656" y="0"></use>
         |</g>
         |</svg>""".stripMargin
  )

  it should "typeset quadratic using MathJax 3" in test("quadratic", useMathJaxV3 = true, "x = {-b \\pm \\sqrt{b^2-4ac} \\over 2a}.",
    s"""|<svg style="vertical-align: -0.439ex;" xmlns="http://www.w3.org/2000/svg" width="29.363ex" height="2.326ex" role="img" focusable="false" viewBox="0 -834 12978.6 1028" xmlns:xlink="http://www.w3.org/1999/xlink"><defs><path id="MJX-1-TEX-I-1D465" d="M52 289Q59 331 106 386T222 442Q257 442 286 424T329 379Q371 442 430 442Q467 442 494 420T522 361Q522 332 508 314T481 292T458 288Q439 288 427 299T415 328Q415 374 465 391Q454 404 425 404Q412 404 406 402Q368 386 350 336Q290 115 290 78Q290 50 306 38T341 26Q378 26 414 59T463 140Q466 150 469 151T485 153H489Q504 153 504 145Q504 144 502 134Q486 77 440 33T333 -11Q263 -11 227 52Q186 -10 133 -10H127Q78 -10 57 16T35 71Q35 103 54 123T99 143Q142 143 142 101Q142 81 130 66T107 46T94 41L91 40Q91 39 97 36T113 29T132 26Q168 26 194 71Q203 87 217 139T245 247T261 313Q266 340 266 352Q266 380 251 392T217 404Q177 404 142 372T93 290Q91 281 88 280T72 278H58Q52 284 52 289Z"></path><path id="MJX-1-TEX-N-3D" d="M56 347Q56 360 70 367H707Q722 359 722 347Q722 336 708 328L390 327H72Q56 332 56 347ZM56 153Q56 168 72 173H708Q722 163 722 153Q722 140 707 133H70Q56 140 56 153Z"></path><path id="MJX-1-TEX-N-2212" d="M84 237T84 250T98 270H679Q694 262 694 250T679 230H98Q84 237 84 250Z"></path><path id="MJX-1-TEX-I-1D44F" d="M73 647Q73 657 77 670T89 683Q90 683 161 688T234 694Q246 694 246 685T212 542Q204 508 195 472T180 418L176 399Q176 396 182 402Q231 442 283 442Q345 442 383 396T422 280Q422 169 343 79T173 -11Q123 -11 82 27T40 150V159Q40 180 48 217T97 414Q147 611 147 623T109 637Q104 637 101 637H96Q86 637 83 637T76 640T73 647ZM336 325V331Q336 405 275 405Q258 405 240 397T207 376T181 352T163 330L157 322L136 236Q114 150 114 114Q114 66 138 42Q154 26 178 26Q211 26 245 58Q270 81 285 114T318 219Q336 291 336 325Z"></path><path id="MJX-1-TEX-I-1D45D" d="M23 287Q24 290 25 295T30 317T40 348T55 381T75 411T101 433T134 442Q209 442 230 378L240 387Q302 442 358 442Q423 442 460 395T497 281Q497 173 421 82T249 -10Q227 -10 210 -4Q199 1 187 11T168 28L161 36Q160 35 139 -51T118 -138Q118 -144 126 -145T163 -148H188Q194 -155 194 -157T191 -175Q188 -187 185 -190T172 -194Q170 -194 161 -194T127 -193T65 -192Q-5 -192 -24 -194H-32Q-39 -187 -39 -183Q-37 -156 -26 -148H-6Q28 -147 33 -136Q36 -130 94 103T155 350Q156 355 156 364Q156 405 131 405Q109 405 94 377T71 316T59 280Q57 278 43 278H29Q23 284 23 287ZM178 102Q200 26 252 26Q282 26 310 49T356 107Q374 141 392 215T411 325V331Q411 405 350 405Q339 405 328 402T306 393T286 380T269 365T254 350T243 336T235 326L232 322Q232 321 229 308T218 264T204 212Q178 106 178 102Z"></path><path id="MJX-1-TEX-I-1D45A" d="M21 287Q22 293 24 303T36 341T56 388T88 425T132 442T175 435T205 417T221 395T229 376L231 369Q231 367 232 367L243 378Q303 442 384 442Q401 442 415 440T441 433T460 423T475 411T485 398T493 385T497 373T500 364T502 357L510 367Q573 442 659 442Q713 442 746 415T780 336Q780 285 742 178T704 50Q705 36 709 31T724 26Q752 26 776 56T815 138Q818 149 821 151T837 153Q857 153 857 145Q857 144 853 130Q845 101 831 73T785 17T716 -10Q669 -10 648 17T627 73Q627 92 663 193T700 345Q700 404 656 404H651Q565 404 506 303L499 291L466 157Q433 26 428 16Q415 -11 385 -11Q372 -11 364 -4T353 8T350 18Q350 29 384 161L420 307Q423 322 423 345Q423 404 379 404H374Q288 404 229 303L222 291L189 157Q156 26 151 16Q138 -11 108 -11Q95 -11 87 -5T76 7T74 17Q74 30 112 181Q151 335 151 342Q154 357 154 369Q154 405 129 405Q107 405 92 377T69 316T57 280Q55 278 41 278H27Q21 284 21 287Z"></path><path id="MJX-1-TEX-I-1D460" d="M131 289Q131 321 147 354T203 415T300 442Q362 442 390 415T419 355Q419 323 402 308T364 292Q351 292 340 300T328 326Q328 342 337 354T354 372T367 378Q368 378 368 379Q368 382 361 388T336 399T297 405Q249 405 227 379T204 326Q204 301 223 291T278 274T330 259Q396 230 396 163Q396 135 385 107T352 51T289 7T195 -10Q118 -10 86 19T53 87Q53 126 74 143T118 160Q133 160 146 151T160 120Q160 94 142 76T111 58Q109 57 108 57T107 55Q108 52 115 47T146 34T201 27Q237 27 263 38T301 66T318 97T323 122Q323 150 302 164T254 181T195 196T148 231Q131 256 131 289Z"></path><path id="MJX-1-TEX-I-1D45E" d="M33 157Q33 258 109 349T280 441Q340 441 372 389Q373 390 377 395T388 406T404 418Q438 442 450 442Q454 442 457 439T460 434Q460 425 391 149Q320 -135 320 -139Q320 -147 365 -148H390Q396 -156 396 -157T393 -175Q389 -188 383 -194H370Q339 -192 262 -192Q234 -192 211 -192T174 -192T157 -193Q143 -193 143 -185Q143 -182 145 -170Q149 -154 152 -151T172 -148Q220 -148 230 -141Q238 -136 258 -53T279 32Q279 33 272 29Q224 -10 172 -10Q117 -10 75 30T33 157ZM352 326Q329 405 277 405Q242 405 210 374T160 293Q131 214 119 129Q119 126 119 118T118 106Q118 61 136 44T179 26Q233 26 290 98L298 109L352 326Z"></path><path id="MJX-1-TEX-I-1D45F" d="M21 287Q22 290 23 295T28 317T38 348T53 381T73 411T99 433T132 442Q161 442 183 430T214 408T225 388Q227 382 228 382T236 389Q284 441 347 441H350Q398 441 422 400Q430 381 430 363Q430 333 417 315T391 292T366 288Q346 288 334 299T322 328Q322 376 378 392Q356 405 342 405Q286 405 239 331Q229 315 224 298T190 165Q156 25 151 16Q138 -11 108 -11Q95 -11 87 -5T76 7T74 17Q74 30 114 189T154 366Q154 405 128 405Q107 405 92 377T68 316T57 280Q55 278 41 278H27Q21 284 21 287Z"></path><path id="MJX-1-TEX-I-1D461" d="M26 385Q19 392 19 395Q19 399 22 411T27 425Q29 430 36 430T87 431H140L159 511Q162 522 166 540T173 566T179 586T187 603T197 615T211 624T229 626Q247 625 254 615T261 596Q261 589 252 549T232 470L222 433Q222 431 272 431H323Q330 424 330 420Q330 398 317 385H210L174 240Q135 80 135 68Q135 26 162 26Q197 26 230 60T283 144Q285 150 288 151T303 153H307Q322 153 322 145Q322 142 319 133Q314 117 301 95T267 48T216 6T155 -11Q125 -11 98 4T59 56Q57 64 57 83V101L92 241Q127 382 128 383Q128 385 77 385H26Z"></path><path id="MJX-1-TEX-N-32" d="M109 429Q82 429 66 447T50 491Q50 562 103 614T235 666Q326 666 387 610T449 465Q449 422 429 383T381 315T301 241Q265 210 201 149L142 93L218 92Q375 92 385 97Q392 99 409 186V189H449V186Q448 183 436 95T421 3V0H50V19V31Q50 38 56 46T86 81Q115 113 136 137Q145 147 170 174T204 211T233 244T261 278T284 308T305 340T320 369T333 401T340 431T343 464Q343 527 309 573T212 619Q179 619 154 602T119 569T109 550Q109 549 114 549Q132 549 151 535T170 489Q170 464 154 447T109 429Z"></path><path id="MJX-1-TEX-N-34" d="M462 0Q444 3 333 3Q217 3 199 0H190V46H221Q241 46 248 46T265 48T279 53T286 61Q287 63 287 115V165H28V211L179 442Q332 674 334 675Q336 677 355 677H373L379 671V211H471V165H379V114Q379 73 379 66T385 54Q393 47 442 46H471V0H462ZM293 211V545L74 212L183 211H293Z"></path><path id="MJX-1-TEX-I-1D44E" d="M33 157Q33 258 109 349T280 441Q331 441 370 392Q386 422 416 422Q429 422 439 414T449 394Q449 381 412 234T374 68Q374 43 381 35T402 26Q411 27 422 35Q443 55 463 131Q469 151 473 152Q475 153 483 153H487Q506 153 506 144Q506 138 501 117T481 63T449 13Q436 0 417 -8Q409 -10 393 -10Q359 -10 336 5T306 36L300 51Q299 52 296 50Q294 48 292 46Q233 -10 172 -10Q117 -10 75 30T33 157ZM351 328Q351 334 346 350T323 385T277 405Q242 405 210 374T160 293Q131 214 119 129Q119 126 119 118T118 106Q118 61 136 44T179 26Q217 26 254 59T298 110Q300 114 325 217T351 328Z"></path><path id="MJX-1-TEX-I-1D450" d="M34 159Q34 268 120 355T306 442Q362 442 394 418T427 355Q427 326 408 306T360 285Q341 285 330 295T319 325T330 359T352 380T366 386H367Q367 388 361 392T340 400T306 404Q276 404 249 390Q228 381 206 359Q162 315 142 235T121 119Q121 73 147 50Q169 26 205 26H209Q321 26 394 111Q403 121 406 121Q410 121 419 112T429 98T420 83T391 55T346 25T282 0T202 -11Q127 -11 81 37T34 159Z"></path><path id="MJX-1-TEX-I-1D45C" d="M201 -11Q126 -11 80 38T34 156Q34 221 64 279T146 380Q222 441 301 441Q333 441 341 440Q354 437 367 433T402 417T438 387T464 338T476 268Q476 161 390 75T201 -11ZM121 120Q121 70 147 48T206 26Q250 26 289 58T351 142Q360 163 374 216T388 308Q388 352 370 375Q346 405 306 405Q243 405 195 347Q158 303 140 230T121 120Z"></path><path id="MJX-1-TEX-I-1D463" d="M173 380Q173 405 154 405Q130 405 104 376T61 287Q60 286 59 284T58 281T56 279T53 278T49 278T41 278H27Q21 284 21 287Q21 294 29 316T53 368T97 419T160 441Q202 441 225 417T249 361Q249 344 246 335Q246 329 231 291T200 202T182 113Q182 86 187 69Q200 26 250 26Q287 26 319 60T369 139T398 222T409 277Q409 300 401 317T383 343T365 361T357 383Q357 405 376 424T417 443Q436 443 451 425T467 367Q467 340 455 284T418 159T347 40T241 -11Q177 -11 139 22Q102 54 102 117Q102 148 110 181T151 298Q173 362 173 380Z"></path><path id="MJX-1-TEX-I-1D452" d="M39 168Q39 225 58 272T107 350T174 402T244 433T307 442H310Q355 442 388 420T421 355Q421 265 310 237Q261 224 176 223Q139 223 138 221Q138 219 132 186T125 128Q125 81 146 54T209 26T302 45T394 111Q403 121 406 121Q410 121 419 112T429 98T420 82T390 55T344 24T281 -1T205 -11Q126 -11 83 42T39 168ZM373 353Q367 405 305 405Q272 405 244 391T199 357T170 316T154 280T149 261Q149 260 169 260Q282 260 327 284T373 353Z"></path><path id="MJX-1-TEX-N-2E" d="M78 60Q78 84 95 102T138 120Q162 120 180 104T199 61Q199 36 182 18T139 0T96 17T78 60Z"></path></defs><g stroke="currentColor" fill="currentColor" stroke-width="0" transform="scale(1,-1)"><g data-mml-node="math"><g data-mml-node="mi"><use data-c="1D465" xlink:href="#MJX-1-TEX-I-1D465"></use></g><g data-mml-node="mo" transform="translate(849.8,0)"><use data-c="3D" xlink:href="#MJX-1-TEX-N-3D"></use></g><g data-mml-node="TeXAtom" data-mjx-texclass="ORD" transform="translate(1905.6,0)"><g data-mml-node="mo"><use data-c="2212" xlink:href="#MJX-1-TEX-N-2212"></use></g><g data-mml-node="mi" transform="translate(778,0)"><use data-c="1D44F" xlink:href="#MJX-1-TEX-I-1D44F"></use></g><g data-mml-node="mi" transform="translate(1207,0)"><use data-c="1D45D" xlink:href="#MJX-1-TEX-I-1D45D"></use></g><g data-mml-node="mi" transform="translate(1710,0)"><use data-c="1D45A" xlink:href="#MJX-1-TEX-I-1D45A"></use></g><g data-mml-node="mi" transform="translate(2588,0)"><use data-c="1D460" xlink:href="#MJX-1-TEX-I-1D460"></use></g><g data-mml-node="mi" transform="translate(3057,0)"><use data-c="1D45E" xlink:href="#MJX-1-TEX-I-1D45E"></use></g><g data-mml-node="mi" transform="translate(3517,0)"><use data-c="1D45F" xlink:href="#MJX-1-TEX-I-1D45F"></use></g><g data-mml-node="mi" transform="translate(3968,0)"><use data-c="1D461" xlink:href="#MJX-1-TEX-I-1D461"></use></g><g data-mml-node="TeXAtom" data-mjx-texclass="ORD" transform="translate(4329,0)"><g data-mml-node="msup"><g data-mml-node="mi"><use data-c="1D44F" xlink:href="#MJX-1-TEX-I-1D44F"></use></g><g data-mml-node="mn" transform="translate(462,363) scale(0.707)"><use data-c="32" xlink:href="#MJX-1-TEX-N-32"></use></g></g><g data-mml-node="mo" transform="translate(1087.8,0)"><use data-c="2212" xlink:href="#MJX-1-TEX-N-2212"></use></g><g data-mml-node="mn" transform="translate(2088,0)"><use data-c="34" xlink:href="#MJX-1-TEX-N-34"></use></g><g data-mml-node="mi" transform="translate(2588,0)"><use data-c="1D44E" xlink:href="#MJX-1-TEX-I-1D44E"></use></g><g data-mml-node="mi" transform="translate(3117,0)"><use data-c="1D450" xlink:href="#MJX-1-TEX-I-1D450"></use></g></g><g data-mml-node="mi" transform="translate(7879,0)"><use data-c="1D45C" xlink:href="#MJX-1-TEX-I-1D45C"></use></g><g data-mml-node="mi" transform="translate(8364,0)"><use data-c="1D463" xlink:href="#MJX-1-TEX-I-1D463"></use></g><g data-mml-node="mi" transform="translate(8849,0)"><use data-c="1D452" xlink:href="#MJX-1-TEX-I-1D452"></use></g><g data-mml-node="mi" transform="translate(9315,0)"><use data-c="1D45F" xlink:href="#MJX-1-TEX-I-1D45F"></use></g><g data-mml-node="mn" transform="translate(9766,0)"><use data-c="32" xlink:href="#MJX-1-TEX-N-32"></use></g><g data-mml-node="mi" transform="translate(10266,0)"><use data-c="1D44E" xlink:href="#MJX-1-TEX-I-1D44E"></use></g></g><g data-mml-node="mo" transform="translate(12700.6,0)"><use data-c="2E" xlink:href="#MJX-1-TEX-N-2E"></use></g></g></g></svg>""".stripMargin
   )

