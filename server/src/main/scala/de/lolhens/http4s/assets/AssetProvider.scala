package de.lolhens.http4s.assets

trait AssetProvider {
  def asset(file: String): Option[Asset]

  def exists(file: String): Boolean = asset(file).isDefined

  def ++(assetProvider: AssetProvider): AssetProvider = { file: String =>
    AssetProvider.this.asset(file)
      .orElse(assetProvider.asset(file))
  }
}
