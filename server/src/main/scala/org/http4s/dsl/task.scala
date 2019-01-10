package org.http4s.dsl

import monix.eval.Task
import org.http4s.HttpRoutes
import org.http4s.server.Server

import scala.language.higherKinds

object task extends Http4sDsl[Task] {
  type Service = HttpRoutes[Task]

  implicit class ServerOps(val server: Server[Task]) extends AnyVal {
    def running: Task[Unit] = Task.never[Unit].doOnCancel(server.shutdown)
  }
}
