package org.http4s.dsl

import cats.effect.Effect
import monix.eval.Task
import monix.execution.Scheduler
import org.http4s.HttpService
import org.http4s.server.Server

import scala.language.higherKinds

object task extends Http4sDsl[Task] {
  type Service = HttpService[Task]

  implicit class ServerOps(val server: Server[Task]) extends AnyVal {
    def running: Task[Unit] = Task.never[Unit].doOnCancel(server.shutdown)
  }

  /*implicit class ServerOps2[F[_] : Effect](val server: Server[F]) {
    def running(implicit scheduler: Scheduler): F[Unit] =
      Task.never[Unit].doOnCancel(Task.fromEffect(server.shutdown)).to[F]
  }*/

}
