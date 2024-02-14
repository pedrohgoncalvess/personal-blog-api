package api.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}
import api.utils.{Authentication, exceptionHandlers}
import database.operations.ArticleQ
import org.ektorp.DocumentNotFoundException
import org.json4s.native.Serialization
import scala.util.{Failure, Success}


class Get extends Directives {

  implicit val formats = org.json4s.DefaultFormats

  val dbOperations = new ArticleQ
  val auth = new Authentication

  val route: Route = concat(
    path("articles") {
      handleExceptions(exceptionHandlers.articleExceptionHandler) {
        authenticateBasic(realm = "secure site", auth.myUserPassAuthenticator) { user =>
          authorize(user.admin) {
            get {
              parameter("tag".as[String].optional) { tag =>

                if (tag.orNull == null) {
                  val articlesToReturn = dbOperations.getAllDocuments
                  onComplete(articlesToReturn) {
                    case Success(articles) =>
                      complete(Serialization.write(articles))
                    case Failure(exception) => complete(StatusCodes.InternalServerError, s"Error when getting the articles. Reason: ${exception.getMessage}")
                  }
                }

                else {
                  val articlesToReturn = dbOperations.getDocumentsByTag(tag.get)
                  onComplete(articlesToReturn) {
                    case Success(articles) =>
                      if (articles.length > 0) {
                        val jsonArticles = Serialization.write(articles)
                        complete(jsonArticles)
                      } else {
                        throw new DocumentNotFoundException(null)
                      }
                  }
                }
              }
            }
          }
        }
      }
    },
    path("articles") {
      handleExceptions(exceptionHandlers.articlesExceptionHandler) {
        get {
          parameter("tag".as[String].optional) { tag =>

            if (tag.orNull == null) {
              val articlesToReturn = dbOperations.getAllDocuments
              onComplete(articlesToReturn) {
                case Success(articles) => complete(Serialization.write(articles.filter(article => article.published)))
                case Failure(exception) => complete(StatusCodes.InternalServerError, s"Error when getting the articles. Reason: ${exception.getMessage}")
              }
            }

            else {
              val articlesToReturn = dbOperations.getDocumentsByTag(tag.get)
              onComplete(articlesToReturn) {
                case Success(articles) =>
                  if (articles.length > 0) {
                  val jsonArticles = Serialization.write(articles.filter(article => article.published))
                  complete(jsonArticles)
                } else {
                  throw new DocumentNotFoundException(null)
                }
              }
            }
          }
        }
      }
    }
  )
}
