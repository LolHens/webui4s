package de.lolhens

import java.nio.file.{Files, Paths}

import de.lolhens.webserver.{PublicAssetProvider, ScalaJsScripts, WebServer}
import monix.execution.Scheduler
import org.http4s.dsl.task._
import org.http4s.{HttpService, MediaType}

import scala.collection.JavaConverters._
import scala.collection.immutable.ListMap
import scala.util.Try

object MainServer extends WebServer with PublicAssetProvider {
  lazy val jsService: Service = HttpService {
    case get@GET -> path =>
      val jsPage = ScalaJsScripts.page(
        "client",
        name => s"/public/$name",
        ScalaJsScripts.resourceExistsInClasspath("public")
      )

      Ok(jsPage)
  }

  def explorer: Service = HttpService {
    case GET -> path =>
      Ok(Explorer.show(path.toList))
  }

  override def services: ListMap[String, Service] = super.services ++ ListMap(
    //"/page" -> pageService,
    "/logfile" -> explorer,
    "/" -> jsService
  )

  /*def main(args: Array[String]): Unit = {
    println(ScalaJsScripts.page("client", name => s"/$name"))
    startWebserver()
  }*/
  override def scheduler: Scheduler = Scheduler.global
}