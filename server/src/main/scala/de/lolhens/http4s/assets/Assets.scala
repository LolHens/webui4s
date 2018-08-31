package de.lolhens.http4s.assets

import java.io.InputStream

import monix.eval.Task
import monix.reactive.Observable

trait Assets {
  def inputStream(fileName: String): Option[InputStream]

  def exists(fileName: String): Boolean = inputStream(fileName).isDefined

  def bytes(fileName: String): Option[Task[Array[Byte]]] = inputStream(fileName).map { inputStream =>
    Observable.fromInputStream(inputStream)
      .foldLeftL(Array.empty[Byte])((acc, e) => acc ++ e)
  }
}
