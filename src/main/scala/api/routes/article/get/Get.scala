package api.routes.article.get

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}
import api.routes.admin.article.get.*
import api.utils.AuthValidators.authenticator

import scala.util.{Failure, Success}


class Get extends Directives with GetJsonSupport:

  private val articleIdParameter: Boolean => Route = isAdmin => {
    path("articles" / JavaUUID) { idArticle =>
      get {
        val articleToReturn = getCompleteArticleById(idArticle)
        onComplete(articleToReturn) {
          case Success(Some(article)) =>
            if (isAdmin)
              complete(article)
            else
              if (article.published)
                complete(article)
              else
                complete(StatusCodes.Forbidden)

          case Success(None) => complete(StatusCodes.NotFound)

          case Failure(_) => complete(StatusCodes.InternalServerError, "An error occurred")
        }
      }
    }
  }

  private val tagParameter: Boolean => Route = isAdmin => {
    path("articles") {
      get {
        parameter("tag".as[String].optional) { tag =>

          if (tag.orNull != null) {
            val articlesToReturn = getPreviewArticlesByTag(tag.get, !isAdmin)
            onComplete(articlesToReturn) {
              case Success(articles) =>
                complete(ArticlesPreview(articles.toArray))
              case Failure(_) =>
                complete(StatusCodes.InternalServerError, "Error when getting the articles.")
            }
          }

          else {
            val articlesToReturn = getPreviewArticles(!isAdmin)
            onComplete(articlesToReturn) {
              case Success(articles) =>
                complete(ArticlesPreview(articles.toArray))
              case Failure(_) => complete(StatusCodes.InternalServerError, "Error while fetching articles.")
            }
          }
        }
      }
    }
  }

  val articleIdRoute: Route =
    authenticateOAuth2(realm = "secure site", authenticator) { auth =>
      authorize(auth.isAdmin) {
        articleIdParameter(true)
      }
    } ~ {
      articleIdParameter(false)
    }

  val tagParameterRoute: Route =
    authenticateOAuth2(realm = "secure site", authenticator) { auth =>
      authorize(auth.isAdmin) {
        tagParameter(true)
      }
    } ~ {
      tagParameter(false)
    }
