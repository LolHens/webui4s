package org.http4s.dsl

import cats.Applicative
import org.http4s.headers.`Content-Type`
import org.http4s.{Charset, EntityEncoder, EntityEncoderInstances, MediaType}
import scalatags.Text.TypedTag

import scala.language.higherKinds

trait ScalatagsEncoder extends EntityEncoderInstances {
  implicit def scalatagsEncoder[F[_]](implicit F: Applicative[F]): EntityEncoder[F, TypedTag[String]] = {
    val hdr = `Content-Type`(MediaType.`text/html`).withCharset(Charset.`UTF-8`)
    stringEncoder(F, Charset.`UTF-8`).contramap((tag: TypedTag[String]) => tag.render).withContentType(hdr)
  }
}
