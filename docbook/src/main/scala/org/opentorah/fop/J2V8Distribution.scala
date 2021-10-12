package org.opentorah.fop

import org.opentorah.util.Platform
import Platform.{Architecture, Os}

final class J2V8Distribution:

  val os: Platform.Os = Platform.getOs
  val architecture: Platform.Architecture = Platform.getArch

  override def toString: String = s"J2V8 for $os on $architecture ($dependencyNotation!$libraryName)"

  val version: Option[String] = os match
    case Os.Windows | Os.Mac => Some("4.6.0")
    // Note: native library needs to be compatible with the Java code used by the plugin (see build.gradle),
    // so it should probably be 4.6.0 even for Linux, but version of Node in it doesn't work with mathjax-node:
    // mathjax-node/lib/main.js:163: SyntaxError:
    //   Block-scoped declarations (let, const, function, class) not yet supported outside strict mode
    //   for (let key in paths) {
    // Conclusion: I have to use 4.8.0 on Linux and in build.gradle, so this probably won't work on any other platform...
    // and even on Linux, if I run two tests that load the library, Gradle demon crashes (although it didn't before...).
    // Real conclusion: do not use J2V8 ):
    case Os.Linux => Some("4.8.0")
    case _ => None

  val osName: String = os match
    case Os.Windows => "win32"
    case Os.Mac     => "macosx"
    case Os.Linux   => "linux"
    case _          => throw IllegalArgumentException()

  val archName: String = architecture match
    case Architecture.i686   => "x86"
    case Architecture.x86_64 => "x86_64"
    case Architecture.amd64  => "x86_64"
    case _                            => throw IllegalArgumentException()

  def dependencyNotation: String =
    s"com.eclipsesource.j2v8:j2v8_${osName}_$archName:${version.get}"

  def libraryName: String =
    s"libj2v8_${osName}_$archName.${os.libraryExtension}"
