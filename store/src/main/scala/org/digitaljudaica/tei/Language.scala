package org.digitaljudaica.tei

import org.digitaljudaica.xml.{ContentType, Descriptor, Xml}

final case class Language(
  ident: String,
  usage: Option[Int],
  text: Option[String]
)

object Language extends Descriptor[Language](
  elementName = "language",
  contentType = ContentType.Mixed,
  contentParser = for {
    ident <- Xml.attribute.required("ident")
    usage <- Xml.attribute.optional.positiveInt("usage")
    text <- Xml.text.optional
  } yield new Language(
    ident,
    usage,
    text
  )
)
