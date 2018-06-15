package de.lolhens.webserver

import monix.eval.Task
import monix.execution.Scheduler
import org.http4s.dsl.task._
import org.http4s.server.Server
import org.http4s.server.blaze.BlazeBuilder

import scala.concurrent.duration.Duration

abstract class WebServer {
  def services: Seq[(String, Service)]

  def start(port: Int, host: String): Task[(Server[Task], Task[Unit])] =
    for {
      builder <-
        Task.deferAction { implicit scheduler =>
          Task.now(BlazeBuilder[Task])
        }
      server <-
        services.foldLeft(builder)((builder, service) =>
          builder.mountService(service._2, service._1)
        )
          .bindHttp(port, host)
          .start
    } yield
      server -> Task.never[Unit].doOnCancel(server.shutdown)

  def scheduler: Scheduler

  def main(args: Array[String]): Unit = {
    implicit val implicitScheduler: Scheduler = scheduler
    start(8080, "localhost")
      .flatMap(_._2).runSyncUnsafe(Duration.Inf)
  }
}
