package api.routes.admin.article


import java.util.UUID
import scala.util.{Failure, Success}

import akka.http.scaladsl.server.{Directives, Route}
import akka.http.scaladsl.model.StatusCodes

import database.models.pub.operations.{getArticleById, getArticleTags}
import api.utils.AuthValidators.authenticator
import api.routes.article.{ArticleComplete, GetJsonSupport}
import database.models.sys.operations.getUserById


class Get extends Directives with GetJsonSupport:

  val route: Route =
    path("article" / JavaUUID) { idArticle =>
        authenticateOAuth2(realm = "secure site", authenticator) { auth =>
          authorize(auth.isAdmin) {
            get {
              val documentToReturn = getArticleById(idArticle)
              onComplete(documentToReturn) {
                case Success(article) =>
                  if (article.orNull != null)
                    val artTagsOpr = getArticleTags(article.get.id.get)
                    onComplete(artTagsOpr) {
                      case Success(artTags) =>
                        onComplete(getUserById(article.get.id_user)) {
                          case Success(user) =>
                            complete(ArticleComplete(
                              id = article.get.id.get,
                              title = article.get.title,
                              description = article.get.description,
                              tags = artTags.map(_.name).toArray,
                              text_en = article.get.text_en,
                              text_pt = article.get.text_pt,
                              created_at = article.get.created_at.get,
                              author = user.get.name,
                              updated_at = article.get.updated_at,
                              published = article.get.published,
                              published_at = article.get.published_at
                            ))
                          case Failure(_) => complete(StatusCodes.InternalServerError, "Error while processing entity.")
                        }
                      case Failure(_) =>
                        complete(StatusCodes.InternalServerError, "Error while processing entity.")
                    }
                  else
                    complete(StatusCodes.NotFound, "Article not found.")
                case Failure(_) => 
                  complete(StatusCodes.InternalServerError, "An error occurred.")
            }
          }
        }
      }
    }
