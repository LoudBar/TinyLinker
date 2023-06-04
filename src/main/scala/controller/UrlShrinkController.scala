package controller

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import service.UrlShrinkService
import akka.event.Logging
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class UrlShrinkController(implicit system: ActorSystem, executionContext: ExecutionContext) {
  private val logger = Logging(system, getClass)

  def shortenUrl(originalUrl: String): Route = {
    val request = HttpRequest(HttpMethods.HEAD, originalUrl)
    val responseFuture = Http().singleRequest(request)

    onComplete(responseFuture) {
      case Success(response) =>
        if (response.status.isSuccess()) {
          val shortenedUrlFuture = UrlShrinkService.shrinkUrl(originalUrl)
          onSuccess(shortenedUrlFuture) { shortenedUrl =>
            complete(shortenedUrl)
          }
        } else {
          logger.warning("Resource with specified address is not available")
          complete(StatusCodes.BadRequest, "Resource with specified address is not available")
        }

      case Failure(ex) =>
        logger.error(s"Unexpected error: ${ex.getMessage}")
        complete(StatusCodes.InternalServerError, s"An error occurred: ${ex.getMessage}")
    }
  }

  def redirectToOriginalUrl(shortenedUrl: String): Route = {
    val shortUrl = s"http://localhost:8080/$shortenedUrl"
    val originalUrlFuture = UrlShrinkService.retrieveOriginalUrl(s"http://localhost:8080/$shortenedUrl")
    onSuccess(originalUrlFuture) {
      case Some(originalUrl) => redirect(originalUrl, StatusCodes.Found)
      case None =>
        logger.error(s"URL not found for shortened URL: $shortUrl")
        complete(StatusCodes.NotFound)
    }
  }
}

class UrlShrinkRoutes(urlShrinkController: UrlShrinkController) {
  val routes = {
    path("shorten") {
      post {
        entity(as[String]) { originalUrl =>
          urlShrinkController.shortenUrl(originalUrl)
        }
      }
    } ~ path(Segment) { shortenedUrl =>
      get {
        urlShrinkController.redirectToOriginalUrl(shortenedUrl)
      }
    }
  }
}
