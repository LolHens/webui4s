package de.lolhens.ui

import de.lolhens.ui.pages.Page
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import org.http4s.HttpService
import org.http4s.dsl.impl.Path
import org.http4s.dsl.task._
import org.http4s.server.blaze.BlazeBuilder

import scala.concurrent.duration.Duration

object Webserver {
  lazy val pageService: Service = HttpService {
    case get@GET -> path if path.startsWith(Page.Test.path) =>
      val ending = Path(path.toList.drop(Page.Test.path.toList.size))

      val output =
        s"""Test ${ending.toString}
           |${get.multiParams}""".stripMargin

      val response = Ok.apply(output)
      response
  }

  def main(args: Array[String]): Unit = {
    val server = BlazeBuilder[Task].bindHttp(8080, "localhost").mountService(pageService, "/").start
      .flatMap(server => Task.never[Unit].doOnCancel(server.shutdown))

    server.runSyncUnsafe(Duration.Inf)
  }

}
