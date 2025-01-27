package database.models.pub.operations

import scala.concurrent.Future
import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global

import slick.jdbc.PostgresProfile.api.*
import slick.jdbc.GetResult

import database.Connection.db
import database.models.pub.TagTable.tagTable
import database.models.pub.ArticleTagTable.articleTagTable
import database.models.pub.{Article, ArticleTag, TagT}


def getArticleTags(idArticle: UUID): Future[Seq[TagT]] =
    db.run((for {
        articleTag <- articleTagTable if articleTag.id_article === idArticle
        tag <- tagTable if tag.id === articleTag.id_tag
    } yield tag).result)


def getArticlesByTag(t: String): Future[Seq[Article]] = {
  val query =
    sql"""
      select
        a.id as article_id,
        a.id_user,
        a.title,
        a.description,
        a.text_pt,
        a.text_en,
        a.published,
        a.created_at,
        a.updated_at,
        a.published_at
      from pub.article a
      join pub.article_tag at on at.id_article = a.id
      join pub.tag t on t.id = at.id_tag
      where t.name = $t
    """

  db.run(query.as[(String, String, String, Option[String], Option[String], String, Boolean, Option[java.sql.Timestamp], Option[java.sql.Timestamp], Option[java.sql.Timestamp])]).map { rows =>
    rows.map { case (id, userId, title, desc, textPt, textEn, pub, created, updated, publishedAt) =>
      Article(
        id = Some(UUID.fromString(id)),
        id_user = UUID.fromString(userId),
        title = title,
        description = desc,
        text_pt = textPt,
        text_en = textEn,
        published = pub,
        created_at = created.map(_.toLocalDateTime),
        updated_at = updated.map(_.toLocalDateTime),
        published_at = publishedAt.map(_.toLocalDateTime)
      )
    }
  }
}


def createRelationTagArticle(a: ArticleTag): Future[Int] = db.run(articleTagTable += a)