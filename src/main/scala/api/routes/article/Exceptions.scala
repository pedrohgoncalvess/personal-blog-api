package api.routes.article

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{complete, extractUri}
import akka.http.scaladsl.server.ExceptionHandler


case class NotFoundArticle() extends Exception


implicit def getExceptionHandler: ExceptionHandler =
  ExceptionHandler {
    case _: NotFoundArticle =>
      extractUri { _ =>
        complete(StatusCodes.NotFound, s"Not found article.")
      }
  }

