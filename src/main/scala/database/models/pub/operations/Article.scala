package database.models.pub.operations

import database.models.pub.Article
import database.models.pub.ArticleTable.articleTable
import database.Connection.db

import scala.concurrent.Future
import slick.jdbc.PostgresProfile.api.*

import java.time.LocalDateTime
import java.util.UUID


def getAllArticles: Future[Seq[Article]] = db.run(articleTable.result)


def getArticleById(id: UUID): Future[Option[Article]] = db.run(articleTable.filter(_.id===id).result.headOption)


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