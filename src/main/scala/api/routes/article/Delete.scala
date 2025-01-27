package api.routes.article

import scala.util.{Failure, Success}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}
import api.utils.AuthValidators.authenticator
import database.models.pub.operations.deleteArticleById


class Delete extends Directives with ManipulateJsonSupport:

  val route: Route =
    path("article") {
      delete {
        authenticateOAuth2(realm = "secure site", authenticator) { auth =>
          authorize(auth.isAdmin) {
              entity(as[DeleteArticle]) { deleteArt =>
                val deleteResultOperation = deleteArticleById(deleteArt.id)
                onComplete(deleteResultOperation) {
                  case Success(_) => complete(s"Article deleted.")
                  case Failure(exception) => complete(StatusCodes.InternalServerError, "An error occurred while deleting article. Try later.")
            }
          }
        }
      }
    }
  }
