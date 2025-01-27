package api.routes.article.get

import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import database.models.pub.{Article, TagT}
import database.models.pub.operations.{getAllArticles, getArticleById, getArticleTags, getArticlesByTag}
import database.models.sys.User
import database.models.sys.operations.getUserById


def getCompleteArticleById(idArticle: UUID): Future[Option[ArticleComplete]] =
  val fArticle: Future[Option[Article]] = getArticleById(idArticle)
  val fTags: Future[Seq[TagT]] = getArticleTags(idArticle)
  val fUser: Future[Option[User]] = fArticle.flatMap {
    a =>
      if (a.getOrElse(None) == None) {
        Future.successful(None)
      }
      getUserById(a.get.id_user)
  }

  for {
    article <- fArticle
    tags    <- fTags
    user    <- fUser
  } yield {
    val acArticle: Article = article.get
    Some(ArticleComplete(
      id = acArticle.id.get,
      title = acArticle.title,
      description = acArticle.description,
      tags = if tags.nonEmpty then tags.map(_.name).toArray else Array.empty[String],
      text_pt = acArticle.text_pt,
      text_en = acArticle.text_en,
      author = user.get.name,
      created_at = acArticle.created_at.get,
      updated_at = acArticle.updated_at,
      published = acArticle.published,
      published_at = acArticle.published_at
    ))
  }


def getPreviewArticlesByTag(tag: String, published: Boolean): Future[Seq[ArticlePreview]] =
  val fAllArticles: Future[Seq[Article]] = getArticlesByTag(tag)

  for {
    allArticles <- fAllArticles
    previews <- Future.sequence(
      allArticles.collect {
        case a if (published && a.published) || !published =>
          getArticleTags(a.id.get).map { tags =>
            ArticlePreview(
              a.id.get,
              a.title,
              a.description,
              tags.map(_.name).toArray,
              a.created_at.get
            )
          }
      }
    )
  } yield previews


def getPreviewArticles(published: Boolean): Future[Seq[ArticlePreview]] =
  val fAllArticles: Future[Seq[Article]] = getAllArticles

  for {
    allArticles <- if published then fAllArticles.map(_.filter(_.published)) else fAllArticles
    previews <- Future.sequence(allArticles.map { a =>
      getArticleTags(a.id.get).map { tags =>
        ArticlePreview(
          a.id.get,
          a.title,
          a.description,
          tags.map(_.name).toArray,
          a.created_at.get
        )
      }
    })
  } yield previews