package org.digitaljudaica.xml

import cats.implicits._

object Element {

  val name: Parser[String] = Context.getName

  object nextNested {
    val name: Parser[Option[String]] = Context.getNextNestedElementName

    def nameIs(expected: String): Parser[Boolean] = name.map(_.contains(expected))

    def nameIsNot(expected: String): Parser[Boolean] = nameIs(expected).map(result => !result)

    // TODO simplify
    def nameIsNot(expected: Option[String]): Parser[Boolean] = for {
      nextNestedElementName <- name
    } yield nextNestedElementName.isEmpty || expected.fold(false)(expected => !nextNestedElementName.contains(expected))
  }

  private def optionalElement[A](name: Option[String], parser: Parser[A], charactersAllowed: Boolean): Parser[Option[A]] = for {
    noElement <- nextNested.nameIsNot(name)
    result <- if (noElement) Parser.pure(None) else for {
      next <- Context.takeNextNestedElement
      result <- Context.nested(None, next, parser, charactersAllowed)
    } yield Some(result)
  } yield result

  class Optional(charactersAllowed: Boolean) {
    final def apply[A](name: String, parser: Parser[A]): Parser[Option[A]] =
      apply(name = Some(name), parser)

    final def apply[A](parser: Parser[A]): Parser[Option[A]] =
      apply(name = None, parser)

    final def apply[A](name: Option[String], parser: Parser[A]): Parser[Option[A]] =
      optionalElement[A](name, parser, charactersAllowed)
  }

  class Required(optional: Optional) {
    final def apply[A](parser: Parser[A]): Parser[A] =
      Parser.required(s"element", optional(parser))

    final def apply[A](name: String, parser: Parser[A]): Parser[A] =
      Parser.required(s"element '$name'", optional(name, parser))
  }

  class All(optional: Optional) {
    final def apply[A](name: String, parser: Parser[A]): Parser[Seq[A]] =
      apply(Some(name), parser)

    final def apply[A](parser: Parser[A]): Parser[Seq[A]] =
      apply(None, parser)

    final def apply[A](name: Option[String], parser: Parser[A]): Parser[Seq[A]] = for {
      headOption <- optional(name, parser)
      tail <- if (headOption.isEmpty) Parser.pure(Seq.empty[A]) else apply(name, parser)
      result = headOption.toSeq ++ tail
    } yield result
  }

  object optional extends Optional(charactersAllowed = false)

  object required extends Required(optional)

  object all extends All(optional)

  object characters {
    object optional extends Optional(charactersAllowed = true)

    object required extends Required(optional)

    object all extends All(optional)
  }
}
