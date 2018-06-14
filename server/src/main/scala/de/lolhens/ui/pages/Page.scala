package de.lolhens.ui.pages

import org.http4s.dsl.io._

class Page(val path: Path) {

}

object Page {
  object Test extends Page(Root / "test")
}
