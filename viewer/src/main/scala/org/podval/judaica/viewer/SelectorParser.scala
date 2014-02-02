/*
 *  Copyright 2014 Leonid Dubinsky <dub@podval.org>.
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

package org.podval.judaica.viewer

import org.podval.judaica.xml.Xml.Ops

import scala.xml.Elem


object SelectorParser {

  private abstract class ParsedSelector(override val names: Names, override val selectors: Seq[Selector]) extends Selector



  def parseSelectors(knownSelectors: Set[Selector], xml: Elem): Seq[Selector] = {
    def descendantsOfOne(result: Set[Selector], next: Selector): Set[Selector] = Selector.descendants(result, Set(next))

    Parse.sequence[Elem, Set[Selector], Selector](parseSelector)(descendantsOfOne)(knownSelectors, xml.elemsFilter("selector"))
  }


  private def parseSelector(knownSelectors: Set[Selector], xml: Elem): Selector = {
    def newSelector: Selector = {
      val names = Names(xml)
      val selectors = parseSelectors(knownSelectors, xml)

      if (xml.booleanAttribute("isNumbered"))
        new ParsedSelector(names, selectors) with NumberedSelector
      else
        new ParsedSelector(names, selectors) with NamedSelector
    }

    def referenceToKnownSelector(name: String) = Names.doFind(knownSelectors, name, "selector")

    xml.attributeOption("name").fold(newSelector)(referenceToKnownSelector)
  }
}