package de.lolhens.http4s.assets

trait AssetProvider {
  def asset(file: String): Option[Asset]

  def exists(file: String): Boolean = asset(file).isDefined

  def ++(assetProvider: AssetProvider): AssetProvider = { file: String =>
    AssetProvider.this.asset(file)
      .orElse(assetProvider.asset(file))
  }
}

/*case object PublicAssets extends AssetProvider {
  def path(fileName: String): String = {
    val file = if (fileName.startsWith("/")) fileName.drop(1) else fileName
    s"/$file"
  }

  def asset(fileName: String): Option[Asset] = Asset.fromClasspath(path(fileName))
}*/
