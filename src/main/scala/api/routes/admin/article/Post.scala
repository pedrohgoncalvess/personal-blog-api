package api.routes.admin.article

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives
import api.routes.admin.article.ManipulateJsonSupport
import api.utils.AuthValidators
import api.utils.AuthValidators.authenticator
import database.models.pub.Article
import database.models.pub.operations.createArticle
import database.models.sys.operations.getUserById

import java.time.LocalDateTime
import java.util.UUID
import scala.concurrent.Future
import scala.util.{Failure, Success}


class Post extends Directives with ManipulateJsonSupport {
  
  val route =
    path("article") {
      authenticateOAuth2(realm = "secure site", authenticator) { auth =>
        authorize(auth.isAdmin) {
          post {
            entity(as[CreateArticle]) { article =>
              val newArticleId = UUID.randomUUID()
              val newArticle = Article(
                id = Some(newArticleId),
                id_user = auth.userId,
                title = article.title,
                description = article.description,
                text_pt = article.text_pt,
                text_en = article.text_en.get,
                published = article.published,
                published_at = Some(null),
                created_at = Some(LocalDateTime.now()),
                updated_at = Some(null),
              )
              val resultOfOperation = createArticle(newArticle)
              onComplete(resultOfOperation) {
                case Success(_) => complete(StatusCodes.Created)
                case Failure(exception) => complete(StatusCodes.InternalServerError, exception.getMessage)
              }
            }
          }
        }
      }
    }
}
