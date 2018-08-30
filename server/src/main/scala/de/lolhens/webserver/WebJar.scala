package de.lolhens.webserver

case class WebJar(name: String, version: String) {
  def file(fileName: String): String = s"META-INF/resources/webjars/$name/$version/$fileName"
}
