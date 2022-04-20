package org.opentorah.xml

import org.slf4j.Logger
import org.xml.sax.{InputSource, XMLFilter}

// TODO my Dom doesn't parse comments!!!
// Note: declareNamespace() and setAttribute[s]() modify in-place.
object Dom extends Xml:

  override type Node = org.w3c.dom.Node
  override type Attributes = org.w3c.dom.Element
  override type Text = org.w3c.dom.Text
  override type Comment = org.w3c.dom.Comment

  override protected def loadFromInputSource(
    inputSource: InputSource,
    filters: Seq[XMLFilter],
    resolver: Option[Resolver]
  ): Element =
    val result: javax.xml.transform.dom.DOMResult = new javax.xml.transform.dom.DOMResult

    Saxon.Saxon11.transform(
      filters = filters,
      resolver = resolver,
      stylesheetFile = None,
      inputSource = inputSource,
      result,
      Xml.logger
    )

    result.getNode.asInstanceOf[org.w3c.dom.Document].getDocumentElement

  override protected def loadNodesFromInputSource(
    inputSource: InputSource,
    filters: Seq[XMLFilter],
    resolver: Option[Resolver]
  ): Nodes = ???

  override def isText(node: Node): Boolean = node.isInstanceOf[Text]
  override def asText(node: Node): Text =    node.asInstanceOf[Text]
  override def getText(text: Text): String = text.getData
  override def mkText(text: String, seed: Node): Text = seed.getOwnerDocument.createTextNode(text)

  override def isComment(node: Node): Boolean = node.isInstanceOf[Comment]
  override def mkComment(text: String, seed: Node): Comment = seed.getOwnerDocument.createComment(text)

  override def toString(node: Node): String = node match
    case comment: org.w3c.dom.Comment => s"<!--${comment.getTextContent}-->"
    case node: Node => node.getTextContent

  override def isElement(node: Node): Boolean = node.isInstanceOf[Element]
  override def asElement(node: Node): Element = node.asInstanceOf[Element]
  override def getName(element: Element): String = element.getLocalName
  override def getPrefix(element: Element): Option[String] = Option(element.getPrefix)

  override def getNamespace(attributes: Attributes): Namespace = Namespace(
    uri = attributes.getNamespaceURI,
    prefix = attributes.getPrefix
  )

  override def getNamespaces(attributes: Attributes): Seq[Namespace] =
    for attribute <- listAttributes(attributes, isXmlns = true) yield
      val localName: String = attribute.getLocalName
      val prefix: String = attribute.getPrefix
      Namespace(
        uri = Option(attribute.getValue),
        prefix =
          if localName == Namespace.Xmlns.prefix then
            require(prefix == null)
            None
          else
            require(prefix == Namespace.Xmlns.prefix)
            require((localName != null) && localName.nonEmpty)
            Some(localName)
      )

  override def isNamespaceDeclared(namespace: Namespace, attributes: Attributes): Boolean =
    namespace.attribute.get(Dom)(attributes) == namespace.getUri

  override def declareNamespace(namespace: Namespace, element: Element): Element =
    setAttribute(namespace.attributeValue, element)
    element

  override protected def getAttributeValueString(attribute: Attribute[?], attributes: Attributes): Option[String] =
    val name: String = attribute.name
    val namespace: Namespace = attribute.namespace
    Option(
      if namespace.isDefault then attributes.getAttribute(name)
      else attributes.getAttributeNS(namespace.getUri.orNull, name)
    )

  override def getAttributes(attributes: Attributes): Attribute.StringValues =
    val result = for attribute <- listAttributes(attributes, isXmlns = false) yield
      Attribute(
        name = attribute.getLocalName,
        namespace = Namespace(
          uri = attribute.getNamespaceURI,
          prefix = attribute.getPrefix
        )
      ).optional.withValue(Option(attribute.getValue))

    // Dom does not have attribute ordering, so unfortunately the order in the source file
    // gets lost; at least enforce some definitive ordering so that the files do not change
    // on every pretty-printing:
    result.sortBy(_.attribute.qName)

  private def listAttributes(attributes: Attributes, isXmlns: Boolean): Seq[org.w3c.dom.Attr] =
    val list: org.w3c.dom.NamedNodeMap = attributes.getAttributes
    for
      attribute <- for index: Int <- 0 until list.getLength yield list.item(index).asInstanceOf[org.w3c.dom.Attr]
      if (attribute.getNamespaceURI == Namespace.Xmlns.uri) == isXmlns
    yield attribute

  override protected def setAttribute[T](attribute: Attribute[T], value: T, element: Element): Element =
    val name: String = attribute.name
    val namespace: Namespace = attribute.namespace
    val valueStr: String = attribute.toString(value)
    if namespace.isDefault then element.setAttribute(name, valueStr) else
      // declare the attribute's namespace if it is not declared - unless the attribute *is* a namespace declaration ;)
      if namespace != Namespace.Xmlns then ensureNamespaceDeclared(namespace, element)

      element.setAttributeNS(namespace.getUri.orNull, namespace.qName(name), valueStr)
    element

  override def setAttributes(attributes: Attribute.Values, element: Element): Element = ??? // TODO implement

  override def getChildren(element: Element): Nodes =
    val list: org.w3c.dom.NodeList = element.getChildNodes
    for index <- 0 until list.getLength yield list.item(index)

  // TODO modifies in place...
  override def setChildren(element: Element, children: Nodes): Element =
    for child <- getChildren(element) do element.removeChild(child)
    for child <- children do element.appendChild(child)
    element

  // TODO implement!
  override protected def descendats(nodes: Nodes, elementName: String): Nodes = ???
