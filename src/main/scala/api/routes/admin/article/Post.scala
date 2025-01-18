package api.routes.admin.article


import java.time.LocalDateTime
import java.util.UUID
import scala.util.{Failure, Success}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}

import api.utils.AuthValidators
import api.utils.AuthValidators.authenticator
import database.models.pub.{Article, TagT, ArticleTag}
import database.models.pub.operations.createArticle
import database.models.pub.operations.createTag
import database.models.pub.operations.getAllTags
import database.models.pub.operations.createRelationTagArticle


class Post extends Directives with ManipulateJsonSupport:
  
  val route: Route =
    path("article") {
        post {
          authenticateOAuth2(realm = "secure site", authenticator) { auth =>
            authorize(auth.isAdmin) {
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
                val fAllTags: Future[Seq[TagT]] = getAllTags
                onComplete(fAllTags) {
                  case Success(allTags) =>
                    val tagNames = allTags.map(_.name)
                    val tagsToCreate = article.tags.filter(t => !tagNames.contains(t)).map(
                      t => TagT(Some(UUID.randomUUID()), t, Some(LocalDateTime.now))
                    )
                    val oprCreateTag = for {
                      results <- Future.sequence(tagsToCreate.map(t => createTag(t)))
                    } yield results

                    onComplete(oprCreateTag) {
                      case Success(_) =>
                        val resultOfOperation = createArticle(newArticle)
                        onComplete(resultOfOperation) {
                          case Success(_) =>
                            val cctTags = allTags.concat(tagsToCreate)
                            val relationsToCreate = article.tags.map(
                              t => ArticleTag(Some(UUID.randomUUID()), newArticleId, cctTags.filter(_.name==t).head.id.get)
                            )
                            val oprCreateRelation = for {
                              results <- Future.sequence(relationsToCreate.map(r => createRelationTagArticle(r)))
                            } yield results

                            onComplete(oprCreateRelation) {
                              case Success(_) => complete(StatusCodes.Created) 
                              case Failure(e) => 
                                println(e)
                                complete(StatusCodes.InternalServerError, "Unable to create new articles now, please try again later.")
                      }
                    }
                      case Failure(_) => complete(StatusCodes.InternalServerError, "Unable to create new articles now, please try again later.")
                  }
                }
              }
            }
          }
        }
      }
