package de.lolhens.webserver

import de.lolhens.http4s.assets.{WebJar, WebJars}
import monix.eval.{MVar, Task}
import monix.reactive.Observable
import org.http4s.dsl.task._
import org.http4s.{HttpService, MediaType}

import scala.collection.immutable.ListMap
import scala.ref.SoftReference

trait PublicAssetProvider extends WebServer {
  val jars = WebJars(
    WebJar("bootstrap", "4.1.3"),
    WebJar("jquery", "3.0.0"),
    WebJar("popper.js", "1.14.3")
  )

  lazy val publicAssetService: Service = {
    val cacheTask = MVar[Map[String, SoftReference[Array[Byte]]]](Map.empty).memoize

    HttpService {
      case GET -> httpPath =>
        def responseOption = for {
          path <- Some(httpPath.toString).filter(_.nonEmpty)
          filePath = s"/public$path"
          fileExtension <- Some(filePath.lastIndexOf(".")).filter(_ >= 0).map(i => filePath.drop(i + 1))
          mediaType = MediaType.forExtension(fileExtension).getOrElse(MediaType.`text/plain`)
          inputStream <- Option(getClass.getResourceAsStream(filePath))
          bytesTask = for {
            cacheVar <- cacheTask
            cache <- cacheVar.take
            cachedBytesOption = cache.get(path).flatMap(_.get)
            bytes <- cachedBytesOption.map(Task.now).getOrElse {
              Observable.fromInputStream(inputStream).foldLeftL(Array[Byte]()) { (last, e) =>
                last ++ e
              }
            }
            _ <- cacheVar.put(cache + (path -> SoftReference(bytes)))
          } yield
            bytes
        } yield
          Ok.apply(bytesTask).withType(mediaType)

        def webJar =
          jars.asset(httpPath.toString).map(asset => Ok(asset.bytesTask).withType(asset.mediaType))

        println(httpPath.toString)

        webJar orElse responseOption getOrElse NotFound()
    }
  }

  def service: (String, Service) = "/public" -> publicAssetService

  def services: ListMap[String, Service] = ListMap(service)
}
