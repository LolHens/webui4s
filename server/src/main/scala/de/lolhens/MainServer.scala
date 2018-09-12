package de.lolhens

import de.lolhens.http4s.assets.{AssetProvider, WebJar}
import de.lolhens.webserver.{ScalaJsScripts, WebServer}
import monix.execution.Scheduler
import org.http4s.HttpService
import org.http4s.dsl.task._
import org.http4s.scalatags._
import scalatags.Text.all._

import scala.collection.immutable.ListMap

object MainServer extends WebServer {
  val assets: AssetProvider =
    WebJar("bootstrap", "4.1.3") ++
      WebJar("client", "0.0.0")

  val scalaJsScripts = ScalaJsScripts("client", e => assets.exists("js/" + e))

  lazy val jsService: Service = HttpService {
    case get@GET -> path =>
      val jsPage = scalaJsScripts.page(name => s"/public/$name")

      Ok(jsPage)
  }

  lazy val publicAssetService: Service = HttpService {
    case get@GET -> path =>

      println(path)

      def webJar =
        assets.asset("js" + path.toString).map(asset => Ok(asset.bytesTask).withType(asset.mediaType))

      webJar.get
  }

  def explorer: Service = HttpService {
    case GET -> path =>
      //Ok(Explorer.show(path.toList))
      Ok(
        html(
          head(
            link(rel := "stylesheet", href := "/public/js/bootstrap.js")
          )
        )
      )
  }

  override def services: ListMap[String, Service] = ListMap(
    //"/page" -> pageService,
    "/logfile" -> explorer,
    "/public" -> publicAssetService,
    "/" -> jsService,
  )

  /*def main(args: Array[String]): Unit = {
    println(ScalaJsScripts.page("client", name => s"/$name"))
    startWebserver()
  }*/
  override def scheduler: Scheduler = Scheduler.global
}