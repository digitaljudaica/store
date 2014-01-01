/*
 * Copyright 2012-2013 Leonid Dubinsky <dub@podval.org>.
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

import org.podval.judaica.xml.Load

import scala.xml.Elem

import java.io.File


class Edition private(val work: Work, name: String, metadata: Elem, directory: File) extends Named {

  override val names = Names(name, metadata)

//  def root: Selection
}



object Edition {

  def apply(work: Work, name: String, metadata: File, directory: File): Edition =
    new Edition(work, name, Load.loadFile(metadata, "edition"), directory)
}