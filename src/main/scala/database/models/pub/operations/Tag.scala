package database.models.pub.operations


import scala.concurrent.Future
import java.util.UUID
import java.time.LocalDateTime

import slick.jdbc.PostgresProfile.api.*

import database.Connection.db
import database.models.pub.Article
import database.models.pub.TagT
import database.models.pub.TagTable.tagTable


def getAllTags: Future[Seq[TagT]] = db.run(tagTable.result)


def createTag(t: TagT): Future[Int] = db.run(tagTable += t)