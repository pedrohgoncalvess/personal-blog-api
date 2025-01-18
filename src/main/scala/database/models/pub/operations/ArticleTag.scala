package database.models.pub.operations


import scala.concurrent.Future
import java.util.UUID

import slick.jdbc.PostgresProfile.api.*

import database.Connection.db
import database.models.pub.TagTable.tagTable
import database.models.pub.ArticleTagTable.articleTagTable
import database.models.pub.{Article, ArticleTag}
import database.models.pub.ArticleTable.articleTable


def getArticleTags(articleId: UUID): Future[Seq[String]] =
    db.run((for {
        articleTag <- articleTagTable if articleTag.id_article === articleId
        tag <- tagTable if tag.id === articleTag.id_tag
    } yield tag.name).result)


def getArticlesByTag(tagName:String): Future[Seq[Article]] =
  db.run(( for {
      tag <- tagTable if tag.name === tagName
      articleTag <- articleTagTable if articleTag.id_tag === tag.id
      article <- articleTable if articleTag.id_article == article.id
  } yield article).result)


def createRelationTagArticle(a: ArticleTag): Future[Int] = db.run(articleTagTable += a)