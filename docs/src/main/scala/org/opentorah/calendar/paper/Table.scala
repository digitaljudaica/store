package org.opentorah.calendar.paper

import org.opentorah.docbook.DocBook
import org.opentorah.util.Files
import org.opentorah.xml.ScalaXml
import java.io.File

object Table:
  final class Column[T](val heading: String, /*subheading: String,*/ val f: T => Any)

final class Table[T](rows: Seq[T])(columns: Table.Column[T]*):

  def writeMarkdown(directory: File, name: String): Unit =
    def formatRow(values: Seq[String]): String = values.mkString("|", "|", "|\n")

    val strings = Seq(
      formatRow(columns.map(_.heading)),
      formatRow(columns.map(_ => "---:"))
    ) ++
      rows.map(row => formatRow(columns.map(_.f(row).toString)))

    Files.write(file = File(directory, name + ".md"), content = strings.mkString)

  //             <row>{for (c <- columns) yield <entry>{c.subheading}</entry>}</row>
  def writeDocbook(directory: File, name: String): Unit =
    val element: ScalaXml.Element =
      <informaltable xmlns={DocBook.namespace.uri} version={DocBook.version} frame="all" xml:id={name}>
        <tgroup cols={columns.length.toString}>
          <thead>
            <row>{for c: Table.Column[T] <- columns yield <entry>{c.heading}</entry>}</row>
          </thead>
          <tbody>
            {for r: T <- rows yield
            <row>{for c: Table.Column[T] <- columns yield <entry>{c.f(r)}</entry>}</row>}
          </tbody>
        </tgroup>
      </informaltable>

    Files.write(file = File(directory, name + ".xml"), content = DocBook.prettyPrinter.renderWithHeader(ScalaXml)(element))
