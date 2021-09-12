package org.opentorah.docbook.section

import java.io.File
import org.opentorah.xml.{ScalaXml, Xsl}
import org.opentorah.util.Collections
import Section.Parameters

trait DocBook2 extends Section:

  def defaultVariant: Variant = Variant(this, None)

  def usesIntermediate: Boolean = false

  protected def outputFileExtension: String

  protected def intermediateFileExtension: String = outputFileExtension

  // From general to specific
  final def parameterSections: List[Section] =  commonSections :+ this
  def commonSections: List[CommonSection]

  // Sanity check
  if commonSections.contains(Common) && commonSections.contains(HtmlCommon) &&
    (commonSections != List(Common, HtmlCommon)) then
    throw IllegalArgumentException(s"Wrong section order for $this: $commonSections")

  protected def stylesheetUriName: String

  def usesRootFile: Boolean

  final def mainStylesheet(
    paramsStylesheetName: String,
    stylesheetUriBase: String,
    customStylesheets: Seq[String],
    values: NonOverridableParameters
  ): ScalaXml.Element =
    // xsl:param has the last value assigned to it, so customization must come last;
    // since it is imported (so as not to be overwritten), and import elements must come first,
    // a separate "-param" file is written with the "default" values for the parameters :)

    <xsl:stylesheet xmlns:xsl={Xsl.namespace.uri} version={Xsl.version(usesDocBookXslt2)}>
      <!-- DO NOT EDIT! Generated by the DocBook plugin. -->
      <xsl:import href={s"$stylesheetUriBase/$stylesheetUriName.xsl"}/>
      <xsl:import href={paramsStylesheetName}/>
      <!-- Custom stylesheets -->
      {customStylesheets.map(customStylesheet => <xsl:import href={customStylesheet}/>)}

      <!-- Non-overridable parameters -->
      {DocBook2.parametersBySection(
        parameterSections.map((section: Section) => section.name -> section.nonOverridableParameters(values)))}

      {mainStylesheetBody(values)}
    </xsl:stylesheet>

  protected def mainStylesheetBody(values: NonOverridableParameters): ScalaXml.Nodes

  def paramsStylesheet(parameters: Seq[(String, Parameters)]): ScalaXml.Element =
    <xsl:stylesheet xmlns:xsl={Xsl.namespace.uri} version={Xsl.version(usesDocBookXslt2)}>
      <!-- DO NOT EDIT! Generated by the DocBook plugin. -->
      {DocBook2.parametersBySection(parameters)}
    </xsl:stylesheet>

  def usesCss: Boolean

  final def rootFileNameWithExtension(inputFileName: String, isIntermediate: Boolean): String =
    rootFilename(inputFileName) + "." + (if isIntermediate then intermediateFileExtension else outputFileExtension)

  final def rootFilename(inputFileName: String): String =
    outputFileNameOverride.getOrElse(inputFileName)

  protected def outputFileNameOverride: Option[String] = None

  def copyDestinationDirectoryName: Option[String] = None

  def isPdf: Boolean = false

  def postProcess(
    inputDirectory: File,
    outputFile: File
  ): Unit = {
  }

object DocBook2:

  val all: List[DocBook2] = List(Html, Epub2, Epub3, Pdf, Html2)

  def find(name: String): Option[DocBook2] = all.find(_.name.equalsIgnoreCase(name))

  def forName(name: String): DocBook2 = find(name).getOrElse {
    throw IllegalArgumentException(
      s"""Unsupported output format $name;
         |supported formats are: ${getNames(all)}
         |""".stripMargin
    )
  }

  def getNames(processors: List[DocBook2]): String =
    processors.map(docBook2 => "\"" + docBook2.name +"\"").mkString("[", ", ", "]")

  private def parametersBySection(parameters: Seq[(String, Parameters)]): ScalaXml.Nodes =
    val result = for
      (sectionName: String, sectionParameters: Parameters) <- Collections.pruneSequenceOfMaps(parameters)
      if sectionParameters.nonEmpty
    yield ScalaXml.mkComment(s" $sectionName ") +: toXml(sectionParameters)
    result.flatten

  private def toXml(parameters: Parameters): Seq[ScalaXml.Element] =
    for (name: String, value: String) <- parameters.toSeq yield
      if value.nonEmpty then <xsl:param name={name}>{value}</xsl:param>
      else <xsl:param name={name}/>
