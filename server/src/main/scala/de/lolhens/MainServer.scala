package de.lolhens

import de.lolhens.ui.pages.{Page, ScalaJsScripts}
import de.lolhens.webserver.WebServer
import monix.eval.Task
import monix.execution.Scheduler
import org.http4s.dsl.impl.Path
import org.http4s.dsl.task._
import org.http4s.{HttpService, MediaType}

object MainServer extends WebServer {
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
      val output = ScalaJsScripts.page("client", name => s"/public/$name",
        ScalaJsScripts.resourceExists("public"))

      val response = Ok(output).withType(MediaType.`text/html`)
      response
  }

  //cache files?
  lazy val assetsService: Service = HttpService {
    case GET -> httpPath =>
      val responseOption = for {
        path <- Some(httpPath.toString).filter(_.nonEmpty)
        filePath = s"/public$path"
        fileExtension <- Some(filePath.lastIndexOf(".")).filter(_ >= 0).map(i => filePath.drop(i + 1))
        mediaType = MediaType.forExtension(fileExtension).getOrElse(MediaType.`text/plain`)
        inputStream <- Option(getClass.getResourceAsStream(filePath))
      } yield
        Ok(Task(inputStream)).withType(mediaType)

      responseOption getOrElse NotFound()
  }

  def services: Seq[(String, Service)] = List(
    "/public" -> assetsService,
    "/page" -> pageService,
    "/js" -> jsService
  )

  /*def main(args: Array[String]): Unit = {
    println(ScalaJsScripts.page("client", name => s"/$name"))
    startWebserver()
  }*/
  override def scheduler: Scheduler = Scheduler.global
}
