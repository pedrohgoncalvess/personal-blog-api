package database.models.pub.operations

import api.routes.admin.article.UpdateArticle
import database.models.pub.Article
import database.models.pub.ArticleTable.{ArticleTable, articleTable}
import database.Connection.db
import database.models.pub.TagTable.tagTable

import scala.concurrent.Future
import slick.jdbc.PostgresProfile.api.*
import slick.model.Column

import java.time.LocalDateTime
import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global


def getAllArticles: Future[Seq[Article]] = db.run(articleTable.result)


def getArticleById(id: UUID): Future[Option[Article]] = db.run(articleTable.filter(_.id===id).result.headOption)


def getArticlesByTag(tagName: String): Future[Seq[Article]] =
  val query = for {
    tag <- tagTable if tag.name === tagName
    article <- articleTable if article.id === tag.id_article
  } yield article

  db.run(query.result)


def updateArticle(nArticle: Article): Future[Int] =
  val query = articleTable.filter(_.id === nArticle.id)

  db.run(query.map(article => (
    article.title,
    article.text_en,
    article.text_pt,
    article.description,
    article.published,
    article.updated_at
  )).update((
    nArticle.title,
    nArticle.text_en,
    nArticle.text_pt,
    nArticle.description,
    nArticle.published,
    Some(LocalDateTime.now())
  )))


def createArticle(nArticle: Article): Future[Int] =
  db.run(articleTable += nArticle)
  
def deleteArticleById(id: UUID): Future[Int] =
  db.run(articleTable.filter(_.id==id).delete)