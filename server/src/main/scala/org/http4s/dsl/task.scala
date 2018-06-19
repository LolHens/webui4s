package org.http4s.dsl

import monix.eval.Task
import org.http4s.HttpService
import org.http4s.util.UrlCodingUtils

object task extends Http4sDsl[Task] with ScalatagsEncoder {
  type Service = HttpService[Task]
}
