package api.routes.article

import akka.http.scaladsl.server.{Directives, Route}
import api.utils.{Authentication, exceptionHandlers}
import database.Operations
import org.ektorp.DocumentNotFoundException
import org.json4s.DefaultFormats
import org.json4s.native.Serialization


class Get extends Directives {

  implicit val formats: DefaultFormats.type = org.json4s.DefaultFormats

  val dbOperations = new Operations
  val auth = new Authentication

  val route: Route =
    path("article") {
      handleExceptions(exceptionHandlers.articleExceptionHandler) {
        get {
          parameter("id".as[String]) { id =>

            val articleToReturn = Serialization.write(dbOperations.getDocumentByID(id))
            if (articleToReturn != "null") {
              complete(articleToReturn)
            } else {
              throw new DocumentNotFoundException("Document not found.")
            }
          }
        }
      }
    }
}
