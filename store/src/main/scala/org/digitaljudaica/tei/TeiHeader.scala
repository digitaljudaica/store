package org.digitaljudaica.tei

import org.digitaljudaica.xml.Descriptor

final case class TeiHeader(
  fileDesc: FileDesc,
  encodingDesc: Option[EncodingDesc],
  profileDesc: Option[ProfileDesc],
  xenoData: Option[XenoData],
  revisionDesc: Option[RevisionDesc]
)

object TeiHeader extends Descriptor[TeiHeader](
  elementName = "teiHeader",
  contentParser = for {
    fileDesc <- FileDesc.required
    encodingDesc <- EncodingDesc.optional
    profileDesc <- ProfileDesc.optional
    xenoData <- XenoData.optional
    revisionDesc <- RevisionDesc.optional
  } yield new TeiHeader(
    fileDesc,
    encodingDesc,
    profileDesc,
    xenoData,
    revisionDesc
  ),
  toXml = (value: TeiHeader) =>
    <teiHeader>
      {FileDesc.toXml(value.fileDesc)}
      {EncodingDesc.toXml(value.encodingDesc)}
      {ProfileDesc.toXml(value.profileDesc)}
      {XenoData.toXml(value.xenoData)}
      {RevisionDesc.toXml(value.revisionDesc)}
    </teiHeader>
)
