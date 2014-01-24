/*
 *  Copyright 2011-2013 Leonid Dubinsky <dub@podval.org>.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package org.podval.judaica.importers

import org.podval.judaica.viewer.{Works, Edition, Content}
import org.podval.judaica.xml.Xml

import java.io.File


// TODO switch to using Edition/Storage-based paths
// TODO produce Content, not XML!
// TODO mark-up, do not replace (paragraph, aliya/maftir, sofpasuk, makaf, pasek, brackets around aliya, brackets around kri,)
abstract class Importer(inputDirectoryPath: String, workName: String, editionName: String) {

  private val inputDirectory = new File(inputDirectoryPath)


  protected val edition: Edition = Works.getWorkByName(workName).getEditionByName(editionName)


  final def importBook(inputName: String, outputName: String) {
    val inFile = new File(inputDirectory, inputName + "." + getInputExtension)
    val content = parseBook(inFile)
    val result = processBook(content, edition, outputName)
    val outputFile = edition.storage.storage(outputName).asFile.file
    Xml.print(Content.toXml(result), outputFile)
  }


  protected def getInputExtension: String


  protected def parseBook(file: File): Content


  protected def processBook(content: Content, edition: Edition, outputName: String): Content = content
}
