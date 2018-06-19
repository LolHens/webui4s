package de.lolhens.ui.pages

import scalatags.Text.all._

/**
  * Created by u016595 on 14.12.2016.
  */
object ScalaJsScripts {
  private val defaultResourceExists = resourceExistsInClasspath("")

  def resourceExistsInClasspath(resourcePath: String): String => Boolean = { resource =>
    val cleanResourcePath = resourcePath.dropWhile(_ == '/').reverse.dropWhile(_ == '/').reverse match {
      case "" =>
      case e => s"$e/"
    }
    Option(getClass.getResource("/" + cleanResourcePath + resource)).isDefined
  }

  def page(projectName: String,
           route: String => String,
           exists: String => Boolean = defaultResourceExists): String = html(
    head(
      tags(projectName, route, exists)
    )
  ).render

  private def jsScriptTag(source: String): Tag = script(src := source, `type` := "text/javascript")

  def tags(projectName: String,
           route: String => String,
           exists: String => Boolean = defaultResourceExists): Seq[Tag] =
    assets(projectName, route, exists)
      .map(jsScriptTag)

  def assets(projectName: String,
             route: String => String,
             exists: String => Boolean = defaultResourceExists): Seq[String] =
    scripts(
      projectName,
      exists
    )
      .map(name => route(name))

  def scripts(projectName: String,
              exists: String => Boolean = defaultResourceExists): Seq[String] =
    selectJsDeps(projectName, exists).toSeq ++
      selectLibrary(projectName, exists).toSeq ++
      selectLoader(projectName, exists).toSeq ++
      selectScript(projectName, exists).toSeq

  private def selectLibrary(projectName: String,
                            exists: String => Boolean): Option[String] = {
    val name = projectName.toLowerCase
    Seq(
      s"$name-opt-library.js",
      s"$name-fastopt-library.js"
    )
      .find(exists)
  }

  private def selectJsDeps(projectName: String,
                           exists: String => Boolean): Option[String] = {
    val name = projectName.toLowerCase
    Seq(
      s"$name-jsdeps.min.js",
      s"$name-jsdeps.js"
    )
      .find(exists)
  }

  private def selectLoader(projectName: String,
                           exists: String => Boolean): Option[String] = {
    val name = projectName.toLowerCase
    Seq(
      s"$name-opt-loader.js",
      s"$name-fastopt-loader.js"
    )
      .find(exists)
  }

  private def selectScript(projectName: String,
                           exists: String => Boolean): Option[String] = {
    val name = projectName.toLowerCase
    Seq(
      s"$name-opt.js",
      s"$name-fastopt.js",
      s"$name-opt-bundle.js",
      s"$name-fastopt-bundle.js"
    )
      .find(exists)
  }
}
