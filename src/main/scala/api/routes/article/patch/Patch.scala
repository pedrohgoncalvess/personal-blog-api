package api.routes.article.patch

import java.time.LocalDateTime
import scala.util.{Failure, Success}

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}

import api.routes.article.{ManipulateJsonSupport, UpdateArticle}
import api.utils.AuthValidators
import api.utils.AuthValidators.authenticator
import database.models.pub.Article
import database.models.pub.operations.getArticleById
import database.models.pub.operations.updateArticle as updateForDb


class Patch extends Directives with ManipulateJsonSupport:

  val route: Route =
    path("article") {
      patch {
        authenticateOAuth2(realm = "secure site", authenticator) { auth =>
          authorize(auth.isAdmin) {
              entity(as[UpdateArticle]) { article =>
                onComplete(getArticleById(article.id)) {
                  case Success(oArticle) => 
                    if (oArticle.orNull == null)
                      complete(StatusCodes.NotFound, "Article not found.")
                    else
                      if (oArticle.get.id_user != auth.userId)
                        complete(StatusCodes.Forbidden, "Do not have necessary access.")  //TODO: Implement share administration of articles
                      else
                        val artUpdate = Article(
                          id = oArticle.get.id,
                          title = if article.title.orNull == null then oArticle.get.title else article.title.get,
                          description = if article.description.orNull == null then oArticle.get.description else article.description,
                          text_pt = if article.text_pt.orNull == null then oArticle.get.text_pt else article.text_pt,
                          text_en = if article.text_en.orNull == null then oArticle.get.text_en else article.text_en.get,
                          published = if article.published.orNull == null then oArticle.get.published else article.published.get,
                          updated_at = Some(LocalDateTime.now),
                          published_at = Some(null),
                          id_user = auth.userId
                        )
                        onComplete(updateForDb(artUpdate)) {
                          case Success(_) => complete(StatusCodes.OK)
                          case Failure(exception) => complete(StatusCodes.InternalServerError, "Error while processing entity.")
                        }

                  case Failure(_) => complete(StatusCodes.InternalServerError, "Error while processing entity.") 
            }
          }
        }
      }
    }
  }
