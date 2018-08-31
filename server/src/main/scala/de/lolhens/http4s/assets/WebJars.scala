package de.lolhens.http4s.assets

import monix.execution.atomic.Atomic

case class WebJars(webJars: WebJar*) extends AssetProvider {
  private val cache: Atomic[Map[String, WebJar]] = Atomic(Map.empty[String, WebJar])

  private def search(fileName: String): Option[WebJar] = webJars.find(_.exists(fileName))

  override def asset(fileName: String): Option[Asset] =
    cache.get
      .get(fileName)
      .orElse(search(fileName).map { webJar =>
        cache.transform(_ + (fileName -> webJar))
        webJar
      })
      .flatMap(_.asset(fileName))
}
