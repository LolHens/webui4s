package de.lolhens.http4s.assets

import scala.concurrent.duration.Duration

case class WebJar(name: String, version: String) extends AssetProvider {
  def path(fileName: String): String = {
    val file = if (fileName.startsWith("/")) fileName.drop(1) else fileName
    s"/META-INF/resources/webjars/$name/$version/$file"
  }

  def asset(fileName: String): Option[Asset] = Asset.fromClasspath(path(fileName))
}

object WebJar {
  /*def main(args: Array[String]): Unit = {
    import monix.execution.Scheduler.Implicits.global
    val jars = WebJars(WebJar("bootstrap", "4.1.3"))
    val bytes = jars.asset("js/bootstrap.js").get.bytesTask
      .runSyncUnsafe(Duration.Inf)
    println(new String(bytes))
  }*/
}
