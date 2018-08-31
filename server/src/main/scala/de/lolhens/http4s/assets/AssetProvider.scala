package de.lolhens.http4s.assets

trait AssetProvider {
  def asset(fileName: String): Option[Asset]

  def exists(fileName: String): Boolean = asset(fileName).isDefined
}
