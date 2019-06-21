package org.podval.docbook.gradle.plugin

import java.io.File

import org.gradle.api.Project
import org.podval.docbook.gradle.fop.{Fop, FopPlugin}
import org.podval.docbook.gradle.{jeuclid, mathjax}
import org.podval.docbook.gradle.section.DocBook2
import org.podval.docbook.gradle.util.{Gradle, Logger, Util}
import org.podval.docbook.gradle.xml.{ProcessingInstructionsFilter, Resolver, Xml}
import org.xml.sax.XMLReader

final class ProcessDocBook(
  project: Project,
  substitutions: Map[String, String],
  resolver: Resolver,
  isJEuclidEnabled: Boolean,
  mathJaxTypesetter: Option[mathjax.Typesetter],
  layout: Layout,
  logger: Logger
) {
  def run(
    docBook2: DocBook2,
    prefixed: Boolean,
    documentName: String
  ): Unit = {
    logger.lifecycle(s"DocBook: processing '$documentName' to ${docBook2.name}.")

    val isPdf: Boolean = docBook2.isPdf

    val forDocument: Layout.ForDocument = layout.forDocument(prefixed, documentName)

    val saxonOutputDirectory: File = forDocument.saxonOutputDirectory(docBook2)
    saxonOutputDirectory.mkdirs

    val saxonOutputFile: File = forDocument.saxonOutputFile(docBook2)

    // do not output the 'main' file when chunking in XSLT 1.0
    val outputFile: Option[File] = if (docBook2.usesRootFile) Some(saxonOutputFile) else None

    val xmlReader: XMLReader = Xml.getFilteredXMLReader(
      Seq(new ProcessingInstructionsFilter(substitutions, resolver, logger)) ++
      (if (mathJaxTypesetter.isEmpty || !isPdf) Seq.empty
       else Seq(new mathjax.MathReader(mathJaxTypesetter.get.configuration)))
      // ++ Seq(new TracingFilter)
    )

    // Run Saxon.
    Xml.transform(
      useSaxon9 = docBook2.usesDocBookXslt2,
      resolver,
      inputFile = layout.inputFile(documentName),
      stylesheetFile = layout.stylesheetFile(forDocument.mainStylesheet(docBook2)),
      xmlReader,
      outputFile,
      logger
    )

    copyImagesAndCss(docBook2, saxonOutputDirectory)

    // Post-processing.
    if (docBook2.usesIntermediate) {
      logger.info(s"Post-processing ${docBook2.name}")
      val outputDirectory: File = forDocument.outputDirectory(docBook2)
      outputDirectory.mkdirs

      if (isPdf) {
        val fopPlugin: Option[FopPlugin] =
          if (isJEuclidEnabled) Some(new jeuclid.FopPlugin)
          else mathJaxTypesetter.map(new mathjax.FopPlugin(_))

        Fop.run(
          configurationFile = layout.fopConfigurationFile,
          substitutions: Map[String, String],
          plugin = fopPlugin,
          inputDirectory = saxonOutputDirectory,
          inputFile = saxonOutputFile,
          outputFile = forDocument.outputFile(docBook2),
          logger = logger
        )
      }

      docBook2.postProcess(
        inputDirectory = saxonOutputDirectory,
        outputFile = forDocument.outputFile(docBook2)
      )
    }
  }

  private def copyImagesAndCss(
    docBook2: DocBook2,
    saxonOutputDirectory: File
  ): Unit = {
    val into: File = Util.prefixedDirectory(saxonOutputDirectory, docBook2.copyDestinationDirectoryName)

    logger.info(s"Copying images")
    Gradle.copyDirectory(project,
      into,
      from = layout.imagesDirectory.getParentFile,
      directoryName = layout.imagesDirectoryName
    )

    if (docBook2.usesCss) {
      logger.info(s"Copying CSS")
      Gradle.copyDirectory(project,
        into,
        from = layout.cssDirectory.getParentFile,
        directoryName = layout.cssDirectoryName,
        substitutions = substitutions
      )
    }
  }
}
