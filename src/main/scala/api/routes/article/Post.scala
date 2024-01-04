package api.routes.article

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives
import api.models.{CreateArticle, CreateArticleJsonSupport}
import api.utils.Authentication
import database.Operations
import java.time.LocalDateTime
import scala.concurrent.Future


class Post extends Directives with CreateArticleJsonSupport {

  val dbOperations = new Operations
  val auth = new Authentication

  val route =
    path("article") {
    authenticateBasic(realm = "secure site", auth.myUserPassAuthenticator) { user =>
      authorize(user.admin) {
        post {
          entity(as[CreateArticle]) { article =>
            val newCouchArticle = CreateArticle(
              id = null,
              revision = null,
              name = article.name,
              description = article.description,
              text_pt = article.text_pt,
              text_en = article.text_en,
              tags = article.tags,
              updated_at = Some(LocalDateTime.now().toString)
            )
            val resultOfOperation: Future[Unit] = dbOperations.addNewDocument(newCouchArticle)
            onSuccess(resultOfOperation) {
              complete(StatusCodes.Created)
            }
          }
        }
      }
    }
  }

}
