package de.lolhens.ui.pages

import scalatags.Text.all._

/**
  * Created by u016595 on 14.12.2016.
  */
object ScalaJsScripts {
  def page(name: String, assets: String => String): String = html (
    head(
      apply(name, assets)
    )
  ).render

  def apply(name: String, assets: String => String): Seq[Tag] =
    scripts(
      name,
      assets,
      name => Option(getClass.getResource(s"/public/$name")).isDefined
    )

  private def jsScript(source: String): Tag = script(src := source, `type` := "text/javascript")

  private def scripts(projectName: String,
                      assets: String => String,
                      resourceExists: String => Boolean): Seq[Tag] =
    selectJsDeps(projectName, assets, resourceExists).toSeq ++
      selectLibrary(projectName, assets, resourceExists).toSeq ++
      selectLoader(projectName, assets, resourceExists).toSeq ++
      selectScript(projectName, assets, resourceExists).toSeq

  private def selectLibrary(projectName: String,
                            assets: String => String,
                            resourceExists: String => Boolean): Option[Tag] = {
    val name = projectName.toLowerCase
    Seq(
      s"$name-opt-library.js",
      s"$name-fastopt-library.js"
    )
      .find(resourceExists)
      .map(name => jsScript(assets(name)))
  }

  private def selectJsDeps(projectName: String,
                           assets: String => String,
                           resourceExists: String => Boolean): Option[Tag] = {
    val name = projectName.toLowerCase
    Seq(
      s"$name-jsdeps.min.js",
      s"$name-jsdeps.js"
    )
      .find(resourceExists)
      .map(name => jsScript(assets(name)))
  }

  private def selectLoader(projectName: String,
                           assets: String => String,
                           resourceExists: String => Boolean): Option[Tag] = {
    val name = projectName.toLowerCase
    Seq(
      s"$name-opt-loader.js",
      s"$name-fastopt-loader.js"
    )
      .find(resourceExists)
      .map(name => jsScript(assets(name)))
  }

  private def selectScript(projectName: String,
                           assets: String => String,
                           resourceExists: String => Boolean): Option[Tag] = {
    val name = projectName.toLowerCase
    Seq(
      s"$name-opt.js",
      s"$name-fastopt.js",
      s"$name-opt-bundle.js",
      s"$name-fastopt-bundle.js"
    )
      .find(resourceExists)
      .map(name => jsScript(assets(name)))
  }
}
