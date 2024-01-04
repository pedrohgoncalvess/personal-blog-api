package api.routes.article

import akka.http.scaladsl.server.Directives
import api.models._
import api.utils.{Authentication, exceptionHandlers}
import database.Operations
import org.json4s.DefaultFormats
import scala.concurrent.Future
import scala.util.{Failure, Success}

class Put extends Directives with UpdateArticleJsonSupport {

  implicit val formats: DefaultFormats.type = org.json4s.DefaultFormats

  val dbOperations = new Operations
  val auth = new Authentication

  val route =
    path("article") {
      handleExceptions(exceptionHandlers.articleUpdateExceptionHandler) {
        authenticateBasic(realm = "secure site", auth.myUserPassAuthenticator) { user =>
          authorize(user.admin) {
            put {
              entity(as[UpdateArticle]) { article =>

                val updateResultOperation: Future[Unit] = dbOperations.updateDocumentByID(article)
                onComplete(updateResultOperation) {
                  case Success(_) => complete(s"Document updated.")
                  case Failure(exception) => complete(exception)
                }
              }
            }
          }
        }
      }
    }
}
