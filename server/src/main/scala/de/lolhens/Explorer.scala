package de.lolhens

import java.nio.file.{Files, Path, Paths}
import java.util.stream.Collectors

import org.http4s.util.UrlCodingUtils
import scalatags.Text.TypedTag
import scalatags.Text.all._

import scala.collection.JavaConverters._
import scala.util.Try

object Explorer {
  private def getPath(pathList: List[String]): Path = {
    val pathString = pathList.filterNot(_.isEmpty).mkString("/") + "/"
    Try(Paths.get(s"/$pathString")).getOrElse(Paths.get(pathString))
  }

  def show(pathList: List[String]): TypedTag[String] = {
    val path = getPath(pathList)
    println(path)
    val slash = pathList.lastOption.contains("")
    html(
      if (Files.isRegularFile(path)) showFile(path)
      else showDir(path, slash)
    )
  }

  def showFile(path: Path): Modifier = {
    val content: String = Try(new String(Files.readAllBytes(path))).getOrElse("")
    pre(content)
  }

  def showDir(path: Path, slash: Boolean): Modifier = {
    val hasParent = Option(path.getParent).isDefined
    val files: Seq[Path] = Try(Files.list(path).collect(Collectors.toList()).asScala).getOrElse(Nil)
    val fileNames = files.map(file => path.relativize(file).toString).filterNot(_ == "..")
    val pathName = if (slash) "" else path.getFileName.toString + "/"
    (Seq("..").filter(_ => hasParent) ++ fileNames)
      .map { fileName =>
        a(href := s"./$pathName$fileName", fileName, br)
      }
  }
}
