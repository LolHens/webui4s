package de.lolhens.webserver

import scalatags.Text.TypedTag
import scalatags.Text.all._

/**
  * Created by u016595 on 14.12.2016.
  */
case class ScalaJsScripts(projectName: String,
                          exists: String => Boolean = ScalaJsScripts.defaultResourceExists) {
  private val name = projectName.toLowerCase

  def page(route: String => String,
           headers: Modifier = ""): TypedTag[String] =
    html(head(headers, tags(route)))

  def tags(route: String => String): Seq[Tag] =
    assets(route).map(ScalaJsScripts.jsScriptTag)

  def assets(route: String => String): Seq[String] =
    scripts.map(name => route(name))

  def scripts: Seq[String] =
    selectJsDeps.toSeq ++
      selectLibrary.toSeq ++
      selectLoader.toSeq ++
      selectScript.toSeq

  private def selectLibrary: Option[String] = Seq(
    s"$name-opt-library.js",
    s"$name-fastopt-library.js"
  ).find(exists)

  private def selectJsDeps: Option[String] = Seq(
    s"$name-jsdeps.min.js",
    s"$name-jsdeps.js"
  ).find(exists)

  private def selectLoader: Option[String] = Seq(
    s"$name-opt-loader.js",
    s"$name-fastopt-loader.js"
  ).find(exists)

  private def selectScript: Option[String] = Seq(
    s"$name-opt.js",
    s"$name-fastopt.js",
    s"$name-opt-bundle.js",
    s"$name-fastopt-bundle.js"
  ).find(exists)
}

object ScalaJsScripts {
  val defaultResourceExists: String => Boolean = resourceExistsInClasspath("")

  def resourceExistsInClasspath(resourcePath: String): String => Boolean = { resource =>
    val cleanResourcePath = resourcePath.dropWhile(_ == '/').reverse.dropWhile(_ == '/').reverse match {
      case "" =>
      case e => s"$e/"
    }
    Option(getClass.getResource("/" + cleanResourcePath + resource)).isDefined
  }

  private def jsScriptTag(source: String): Tag = script(src := source, `type` := "text/javascript")

}
