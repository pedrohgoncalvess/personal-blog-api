package database.models.pub.operations


import scala.concurrent.Future
import java.util.UUID

import slick.jdbc.PostgresProfile.api.*

import database.Connection.db
import database.models.pub.Article
import database.models.pub.TagT
import database.models.pub.TagTable.tagTable


def getAllDistinctTags: Future[Seq[String]] = db.run(
  tagTable
    .map(_.name)
    .distinct
    .result
)

def getTagsByArticle(idArticle: UUID): Future[Seq[TagT]] = db.run(
  tagTable.filter(_.id_article===idArticle).result
)

