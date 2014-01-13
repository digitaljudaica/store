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


abstract class Structure(val selector: Selector, val divs: Seq[Div]) extends Named {

  final override def names = selector.names
  final def isNumbered: Boolean = selector.isNumbered

  def asNumbered: NumberedStructure
  def asNamed: NamedStructure

  final def length: Int = divs.length

  final def divByNumber(number: Int): Option[Div] = if ((number < 1) || (number > length)) None else Some(divs(number))
}


final class NamedStructure(
  override val selector: NamedSelector,
  override val divs: Seq[NamedDiv]) extends Structure(selector, divs)
{
  def asNumbered: NumberedStructure = throw new ClassCastException
  def asNamed: NamedStructure = this

  def divByName(name: String): Option[NamedDiv] = Names.find(divs, name)
}


final class NumberedStructure(
  override val selector: NumberedSelector,
  override val divs: Seq[NumberedDiv]) extends Structure(selector, divs)   // TODO something with known length, not Seq...
{
  def asNumbered: NumberedStructure = this
  def asNamed: NamedStructure = throw new ClassCastException
}



trait Structures extends Selectors {

  def structures: Seq[Structure]


  def structureByName(name: String): Option[Structure]
}



object Structure {

  def parseStructures(selectors: Seq[Selector], xmls: Elem): Seq[Structure] = xmls.elemsFilter("structure").map(parseStructure(selectors, _))


  private def parseStructure(selectors: Seq[Selector], xml: Elem): Structure = {
    val selectorName = xml.getAttribute("selector")
    val selector = Exists(Names.find(selectors, selectorName), selectorName, "selector")
    val uncles = selectors.takeWhile(_ != selector)

    val divXmls = xml.elemsFilter("div")

    if (selector.isNumbered) {
      val numberedSelector = selector.asNumbered
      val divs = divXmls.zipWithIndex.map { case (xml, num) => Div.numbered(uncles, numberedSelector, num+1, xml) }
      new NumberedStructure(numberedSelector, divs)
    } else {
      val namedSelector = selector.asNamed
      val divs = divXmls.map(Div.named(uncles, namedSelector, _))
      new NamedStructure(namedSelector, divs)
    }
  }
}
