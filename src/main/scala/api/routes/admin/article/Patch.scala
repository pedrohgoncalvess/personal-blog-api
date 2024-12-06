package api.routes.admin.article


import java.time.LocalDateTime
import scala.concurrent.Future
import scala.util.{Failure, Success}

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}
import org.json4s.DefaultFormats
import api.utils.AuthValidators
import api.utils.AuthValidators.authenticator

import database.models.pub.operations.{getArticleById, updateArticle}
import database.models.pub.Article


class Patch extends Directives with ManipulateJsonSupport {

  val route: Route =
    path("article") {
        authenticateOAuth2(realm = "secure site", authenticator) { auth =>
          authorize(auth.isAdmin) {
            patch {
              entity(as[UpdateArticle]) { article =>
                onComplete(getArticleById(article.id)) {
                  case Success(oArticle) =>
                    if (oArticle.orNull == null)
                      complete(StatusCodes.NotFound, "Article not found.")
                    else
                      val artUpdate = Article(
                        id=Some(null),
                        title=if article.title.orNull != null then oArticle.get.title else article.title.get,
                        description=if article.description.orNull != null then oArticle.get.description else article.description,
                        text_pt=if article.text_en.orNull != null then Some(oArticle.get.text_en) else article.text_en,
                        text_en=if article.text_pt.orNull != null then oArticle.get.text_pt.get else article.text_pt.get,
                        published=if article.published.orNull != null then oArticle.get.published else article.published.get,
                        updated_at=Some(LocalDateTime.now),
                        published_at=Some(null),
                        id_user=auth.userId
                      )
                    complete(StatusCodes.OK)
            }
          }
        }
      }
    }
  }
}
