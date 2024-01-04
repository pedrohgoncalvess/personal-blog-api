package api.routes

import akka.http.scaladsl.server.Directives
import api.utils.exceptionHandlers
import database.Operations
import org.ektorp.DocumentNotFoundException
import org.json4s.native.Serialization


class Get extends Directives {

  implicit val formats = org.json4s.DefaultFormats

  val dbOperations = new Operations

  val route = path("articles") {
    handleExceptions(exceptionHandlers.articlesExceptionHandler) {
      get {
        parameter("tag".as[String]) { tag =>
          validate(tag.isEmpty, null) {
            throw new java.util.NoSuchElementException
          }
          val articlesToReturn = dbOperations.getDocumentsByTag(tag)
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
