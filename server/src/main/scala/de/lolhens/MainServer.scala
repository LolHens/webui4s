package de.lolhens

import de.lolhens.webserver.{PublicAssetProvider, ScalaJsScripts, WebServer}
import monix.execution.Scheduler
import org.http4s.HttpService
import org.http4s.dsl.task._

import scala.collection.immutable.ListMap

object MainServer extends WebServer with PublicAssetProvider {
  val scalaJsScripts = ScalaJsScripts("client", ScalaJsScripts.resourceExistsInClasspath("public"))

  lazy val jsService: Service = HttpService {
    case get@GET -> path =>
      val jsPage = scalaJsScripts.page(name => s"/public/$name")

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