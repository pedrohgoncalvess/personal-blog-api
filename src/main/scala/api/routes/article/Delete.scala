package api.routes.article

import akka.http.scaladsl.server.Directives
import api.utils.{AuthValidators, exceptionHandlers}
import database.models.JsonSupport
import database.operations.ArticleQ
import org.json4s.DefaultFormats
import scala.concurrent.Future
import scala.util.{Failure, Success}


class Delete extends Directives with JsonSupport {

  implicit val formats: DefaultFormats.type = org.json4s.DefaultFormats

  val dbOperations = new ArticleQ
  val auth = new AuthValidators

  val route =
    path("article") {
      delete {
        authenticateOAuth2(realm = "secure site", auth.myUserPassAuthenticator) { auth =>
          authorize(auth) {
            handleExceptions(exceptionHandlers.articleDeleteExceptionHandler) {
              parameter("id".as[String], "revision".as[String]) { (id, revision) =>
                validate(id.isEmpty || revision.isEmpty, null) {
                  throw new java.util.NoSuchElementException
                }
                val deleteResultOperation: Future[Unit] = dbOperations.deleteDocumentByID(id, revision)
                onComplete(deleteResultOperation) {
                  case Success(_) => complete(s"Document deleted")
                  case Failure(exception) => complete(exception)
                }
              }
            }
          }
        }
      }
    }
}
