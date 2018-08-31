package de.lolhens.http4s.assets

trait AssetProvider {
  def asset(file: String): Option[Asset]

  def exists(file: String): Boolean = asset(file).isDefined
}
