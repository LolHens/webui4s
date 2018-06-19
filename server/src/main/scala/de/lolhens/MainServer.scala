package de.lolhens

import de.lolhens.ui.pages.{Page, ScalaJsScripts}
import de.lolhens.webserver.{PublicAssetProvider, WebServer}
import monix.execution.Scheduler
import org.http4s.dsl.impl.Path
import org.http4s.dsl.task._
import org.http4s.{HttpService, MediaType}

import scala.collection.immutable.ListMap

object MainServer extends WebServer with PublicAssetProvider {
  lazy val pageService: Service = HttpService {
    case get@GET -> path if path.startsWith(Page.Test.path) =>
      val ending = Path(path.toList.drop(Page.Test.path.toList.size))

      val output =
        s"""Test ${ending.toString}
           |${get.multiParams}""".stripMargin

      val response = Ok(output)
      response
  }

  lazy val jsService: Service = HttpService {
    case get@GET -> path =>
      val jsPage = ScalaJsScripts.page(
        "client",
        name => s"/public/$name",
        ScalaJsScripts.resourceExistsInClasspath("public")
      )

      Ok(jsPage).withType(MediaType.`text/html`)
  }

  override def services: ListMap[String, Service] = super.services ++ ListMap(
    "/page" -> pageService,
    "/js" -> jsService
  )

  /*def main(args: Array[String]): Unit = {
    println(ScalaJsScripts.page("client", name => s"/$name"))
    startWebserver()
  }*/
  override def scheduler: Scheduler = Scheduler.global
}