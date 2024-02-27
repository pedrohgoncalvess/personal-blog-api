package api.routes.article

import akka.http.scaladsl.server.Directives
import api.json._
import api.utils.{AuthValidators, exceptionHandlers}
import database.operations.ArticleQ
import org.json4s.DefaultFormats
import scala.concurrent.Future
import scala.util.{Failure, Success}

class Put extends Directives with UpdateArticleJsonSupport {

  implicit val formats: DefaultFormats.type = org.json4s.DefaultFormats

  val dbOperations = new ArticleQ
  val auth = new AuthValidators

  val route =
    path("article") {
      handleExceptions(exceptionHandlers.articleUpdateExceptionHandler) {
        authenticateOAuth2(realm = "secure site", auth.myUserPassAuthenticator) { auth =>
          authorize(auth) {
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
