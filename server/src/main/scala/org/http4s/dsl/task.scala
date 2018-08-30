package org.http4s.dsl

import monix.eval.Task
import org.http4s.HttpService
import org.http4s.server.Server

object task extends Http4sDsl[Task] with ScalatagsEncoder {
  type Service = HttpService[Task]

  implicit class ServerOps(val server: Server[Task]) extends AnyVal {
    def running: Task[Unit] = Task.never[Unit].doOnCancel(server.shutdown)
  }

}
