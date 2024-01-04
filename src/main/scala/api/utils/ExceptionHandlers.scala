package api.utils

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{complete, extractUri}
import akka.http.scaladsl.server.ExceptionHandler
import org.ektorp.DocumentNotFoundException

object exceptionHandlers {

  implicit def articleExceptionHandler: ExceptionHandler =
    ExceptionHandler {
      case _: DocumentNotFoundException =>
        extractUri { _ =>
          complete(StatusCodes.NotFound -> "Document not found.")
        }
      case _: java.util.NoSuchElementException =>
        extractUri { _ =>
          complete(StatusCodes.UnprocessableContent -> "Not found name or id parameter.")
        }
    }

  implicit def articlesExceptionHandler: ExceptionHandler =
    ExceptionHandler {
      case _: java.util.NoSuchElementException =>
        extractUri { _ =>
          complete(StatusCodes.UnprocessableContent -> "Not found tag parameter.")
        }
      case _: DocumentNotFoundException =>
        extractUri { _ =>
          complete(StatusCodes.NotFound -> "Document not found.")
        }
    }

  implicit def articleDeleteExceptionHandler: ExceptionHandler =
    ExceptionHandler {
      case _: org.ektorp.DocumentNotFoundException =>
        extractUri { _ =>
          complete(StatusCodes.NotFound -> "Document not found.")
        }
      case _: java.util.NoSuchElementException =>
        extractUri { _ =>
          complete(StatusCodes.UnprocessableContent -> "To delete you need to provide id and revision as parameters.")
        }
    }

  implicit def articleUpdateExceptionHandler: ExceptionHandler = {
    ExceptionHandler {
      case _: org.ektorp.DocumentNotFoundException =>
        extractUri { _ =>
          complete(StatusCodes.NotFound -> "Document not found.")
        }
      case _: java.util.NoSuchElementException =>
        extractUri { _ =>
          complete(StatusCodes.UnprocessableContent -> "To delete you need to provide id and revision as parameters.")
        }
      case message: Exception =>
        extractUri { _ =>
          complete(message)
        }
    }
  }
}


class DocumentWithoutId(message:String) extends Exception(message)
