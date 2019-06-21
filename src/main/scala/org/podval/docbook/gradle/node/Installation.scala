package org.podval.docbook.gradle.node

import java.io.File
import java.nio.file.{Files, Path, Paths}

import scala.sys.process.{Process, ProcessLogger}

final class Installation(
  val distribution: Distribution,
  val nodeRoot: File)
{
  override def toString: String = s"$distribution in $nodeRoot with modules in $nodeModules"

  private def isWindows: Boolean = distribution.isWindows

  val root: File = new File(nodeRoot, distribution.topDirectory)

  private val bin: File = if (distribution.hasBinSubdirectory) new File(root, "bin") else root

  private val nodeExec: File = new File(bin, if (isWindows) "node.exe" else "node")

  private def node(args: String*): String = exec(
    command = nodeExec,
    args.toSeq,
    cwd = None,
    extraEnv = ("NODE_PATH", nodeModules.getAbsolutePath)
  )

  def evaluate(script: String): String = node("--print", script)

  private val npmExec: File = new File(bin, if (isWindows) "npm.cmd" else "npm")

  private def npm(args: String*): String = exec(
    command = npmExec,
    args.toSeq,
    cwd = Some(nodeRoot),
    extraEnv = ("PATH", npmExec.getParentFile.getAbsolutePath)
  )

  val nodeModules: File = new File(nodeRoot, "node_modules")

  def npmInstall(module: String): String =
    npm("install", "--no-save", "--silent", module)


  private def exec(command: File, args: Seq[String], cwd: Option[File], extraEnv: (String, String)*): String = {
    val out = new StringBuilder
    Process(
      command = command.getAbsolutePath +: args,
      cwd,
      extraEnv = extraEnv: _*
    ).!!(ProcessLogger(line => out.append(line), line => out.append(line)))
    out.toString
  }

  def postInstall(): Unit = {
    fixNpmSymlink()
  }

  private def fixNpmSymlink(): Unit = if (!isWindows) {
    val npm: Path = npmExec.toPath
    val deleted: Boolean = Files.deleteIfExists(npm)
    if (deleted) Files.createSymbolicLink(
      npm,
      bin.toPath.relativize(Paths.get(npmScriptFile.getAbsolutePath))
    )
  }

  private def npmScriptFile: File = {
    val lib: String = if (isWindows) "" else "lib/"
    new File(root, s"${lib}node_modules/npm/bin/npm-cli.js")
  }
}
