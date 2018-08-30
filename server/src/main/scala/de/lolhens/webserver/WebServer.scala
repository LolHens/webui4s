package de.lolhens.webserver

import monix.eval.Task
import monix.execution.Scheduler
import org.http4s.dsl.task._
import org.http4s.server.Server
import org.http4s.server.blaze.BlazeBuilder

import scala.collection.immutable.ListMap
import scala.concurrent.duration.Duration

abstract class WebServer {
  def services: ListMap[String, Service]

  def start(f: BlazeBuilder[Task] => BlazeBuilder[Task]): Task[Server[Task]] =
    for {
      builder <-
        Task.deferAction { implicit scheduler =>
          Task.now(BlazeBuilder[Task])
        }
      server <-
        f(services.foldLeft(builder) { (builder, service) =>
          builder.mountService(service._2, service._1)
        }).start
    } yield
      server

  def start(port: Int, host: String): Task[Server[Task]] = start(_.bindHttp(port, host))

  def scheduler: Scheduler

  def runSyncUnsafe(port: Int, host: String): Unit = {
    implicit val implicitScheduler: Scheduler = scheduler
    start(port, host).flatMap(_.running).runSyncUnsafe(Duration.Inf)
  }

  protected def defaultPort = 8080
  protected def defaultHost = "localhost"

  def main(args: Array[String]): Unit = {
    runSyncUnsafe(defaultPort, defaultHost)
  }
}
