package de.lolhens.http4s.assets

import java.io.InputStream

import monix.eval.Task
import monix.execution.atomic.Atomic
import monix.reactive.Observable

import scala.concurrent.duration.Duration

case class WebJar(name: String, version: String) extends Assets {
  def path(fileName: String): String = s"/META-INF/resources/webjars/$name/$version/$fileName"

  def inputStream(fileName: String): Option[InputStream] = Option(getClass.getResourceAsStream(path(fileName)))
}

object WebJar {
  def main(args: Array[String]): Unit = {
    import monix.execution.Scheduler.Implicits.global
    val jars = WebJars(WebJar("bootstrap", "4.1.3"))
    val bytes = jars.bytes("js/bootstrap.js").get
      .runSyncUnsafe(Duration.Inf)
    println(new String(bytes))
  }
}
