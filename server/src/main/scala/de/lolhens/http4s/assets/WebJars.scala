package de.lolhens.http4s.assets

import monix.execution.atomic.Atomic

case class WebJars(webJars: WebJar*) extends AssetProvider {
  private val cache: Atomic[Map[String, WebJar]] = Atomic(Map.empty[String, WebJar])

  private def search(file: String): Option[WebJar] = webJars.find(_.exists(file))

  override def asset(file: String): Option[Asset] =
    cache.get
      .get(file)
      .orElse(search(file).map { webJar =>
        cache.transform(_ + (file -> webJar))
        webJar
      })
      .flatMap(_.asset(file))
}
