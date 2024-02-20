package api.routes.article

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives
import api.json.{CreateArticle, CreateArticleJsonSupport}
import api.utils.Authentication
import database.operations.ArticleQ

import java.time.LocalDateTime
import scala.concurrent.Future
import scala.util.{Failure, Success}


class Post extends Directives with CreateArticleJsonSupport {

  val dbOperations = new ArticleQ
  val auth = new Authentication

  val route =
    path("article") {
      authenticateOAuth2(realm = "secure site", auth.myUserPassAuthenticator) { auth =>
      authorize(auth) {
        post {
          entity(as[CreateArticle]) { article =>
            val newCouchArticle = CreateArticle(
              id = null,
              revision = null,
              name = article.name,
              description = article.description,
              text_pt = article.text_pt,
              text_en = article.text_en,
              published=article.published,
              tags = article.tags,
              updated_at = Some(LocalDateTime.now().toString)
            )
            val resultOfOperation: Future[Unit] = dbOperations.addNewDocument(newCouchArticle)
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
