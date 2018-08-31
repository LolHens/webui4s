package de.lolhens.http4s.assets

import java.io.InputStream

import monix.eval.Task
import monix.reactive.Observable
import org.http4s.MediaType

import scala.language.higherKinds

trait Asset {
  def fileName: String

  def fileExtension: Option[String] = Some(fileName.lastIndexOf(".")).filter(_ >= 0).map(i => fileName.drop(i + 1))

  def mediaType: MediaType =
    fileExtension.flatMap(MediaType.forExtension)
      .getOrElse(MediaType.`text/plain`)

  def inputStream: InputStream

  def bytesTask: Task[Array[Byte]]
}

object Asset {
  def fromClasspath(path: String): Option[Asset] =
    Option(getClass.getResourceAsStream(path))
      .map(_ => Asset.fromInputStream(path.split("/").last, getClass.getResourceAsStream(path)))

  def fromInputStream(name: String, stream: => InputStream): Asset = new Asset {
    override def fileName: String = name

    override def inputStream: InputStream = stream

    override def bytesTask: Task[Array[Byte]] =
      Observable.fromInputStream(inputStream)
        .foldLeftL(Array.empty[Byte])((acc, e) => acc ++ e)
  }
}
