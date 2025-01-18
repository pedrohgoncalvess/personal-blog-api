package api.routes.article

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}
import api.routes.article.{ArticleComplete, ArticlePreview, ArticlesPreview}
import database.models.pub.operations.{getAllArticles, getArticleById, getArticlesByTag, getArticleTags}
import database.models.sys.operations.getUserById

import scala.util.{Failure, Success}


class Get extends Directives with GetJsonSupport {

  val route: Route = concat(
    path("articles") {
        get {
          parameter("tag".as[String].optional) { tag =>

            if (tag.orNull == null) {
              val articlesToReturn = getAllArticles
              onComplete(articlesToReturn) {
                case Success(articles) =>
                  val jsonArticles = articles.filter(_.published).map(a => ArticlePreview(a.id.get, a.title, a.description, Array.empty[String], a.created_at.get))
                  jsonArticles.foreach(println)
                  complete(ArticlesPreview(jsonArticles.toArray))
                case Failure(exception) => complete(StatusCodes.InternalServerError, "Error when getting the articles.")
              }
            }

            else {
              val articlesToReturn = getArticlesByTag(tag.get)
              onComplete(articlesToReturn) {
                case Success(articles) =>
                  if (articles.nonEmpty) {
                  val jsonArticles = articles.filter(_.published).map(a => ArticlePreview(a.id.get, a.title, a.description, Array.empty[String], a.created_at.get))
                  complete(ArticlesPreview(jsonArticles.toArray))
                } else {
                  complete(StatusCodes.NotFound, s"Not found article with tag ${tag.get}")
                }
                case Failure(_) => complete(StatusCodes.InternalServerError, "Error while fetching articles.") 
              }
            }
          }
        }
    },
    path("article" / JavaUUID) { articleID =>
        get {
            val documentToReturn = getArticleById(articleID)
            onComplete(documentToReturn) {
              case Success(article) =>
                if (article.orNull == null) complete(null)
                else if (article.get.published)
                  val art = article.get
                  val tagOpr = getArticleTags(art.id.get)
                  onComplete(tagOpr) {
                    case Success(tags) =>
                      onComplete(getUserById(article.get.id_user)) {
                        case Success(user) =>
                          complete(ArticleComplete(
                            art.id.get,
                            art.title,
                            art.description,
                            tags.map(_.name).toArray,
                            art.text_pt,
                            art.text_en,
                            user.get.name,
                            art.created_at.get,
                            art.updated_at,
                            art.published,
                            art.published_at
                            )
                          )
                        case Failure(_) =>
                          complete(StatusCodes.InternalServerError, "Error reading article.")
                      }
                    case Failure(_) =>
                      complete(StatusCodes.UnprocessableContent, "Error reading article.")
                  }
                  
                else if (!article.get.published) complete(StatusCodes.Forbidden, "Does not have necessary access.")
                else complete(StatusCodes.InternalServerError, "Article cannot be processed.")

              case Failure(exception) => complete(StatusCodes.InternalServerError, "An error occurred")
            }
          }
      }
  )
}
