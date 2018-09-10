package de.lolhens.http4s.assets

case class WebJar(name: String, version: String) extends AssetProvider {
  def path(fileName: String): String = {
    val file = if (fileName.startsWith("/")) fileName.drop(1) else fileName
    s"/META-INF/resources/webjars/$name/$version/$file"
  }

  def asset(fileName: String): Option[Asset] = Asset.fromClasspath(path(fileName))
}
