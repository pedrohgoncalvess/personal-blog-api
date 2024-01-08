package api.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives
import api.models.Article
import api.utils.exceptionHandlers
import database.Operations
import org.ektorp.DocumentNotFoundException
import org.json4s.native.Serialization
import scala.util.{Success, Failure}


class Get extends Directives {

  implicit val formats = org.json4s.DefaultFormats
  import scala.concurrent.ExecutionContext.Implicits.global

  val dbOperations = new Operations

  val route = path("articles") {
    handleExceptions(exceptionHandlers.articlesExceptionHandler) {
      get {
        parameter("tag".as[String].optional) { tag =>

          if (tag.orNull == null) {
            val articlesToReturn = dbOperations.getAllDocuments
            onComplete(articlesToReturn) {
              case Success(articles) => complete(Serialization.write(articles))
              case Failure(exception) => complete(StatusCodes.InternalServerError, s"Erro ao obter os artigos: ${exception.getMessage}")
            }
          }

          else {
            val articlesToReturn = dbOperations.getDocumentsByTag(tag.get)
            if (articlesToReturn.length > 0) {
              val jsonArticles = Serialization.write(articlesToReturn)
              complete(jsonArticles)
            } else {
              throw new DocumentNotFoundException(null)
            }
          }
        }
      }
    }
  }
}
