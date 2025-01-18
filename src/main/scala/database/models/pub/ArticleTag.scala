package database.models.pub


import java.time.LocalDateTime
import java.util.UUID

import slick.jdbc.PostgresProfile.api._


case class ArticleTag(
                    id: Option[UUID],
                    id_article: UUID,
                    id_tag: UUID,
                    created_at: Option[LocalDateTime] = Some(LocalDateTime.now),
                  )


object ArticleTag:
  def tupled = (ArticleTag.apply).tupled


object ArticleTagTable:
  
  class ArticleTagTable(tag: Tag) extends Table[ArticleTag](tag, Some("pub"), "article_tag"):
    def id = column[Option[UUID]]("id", O.PrimaryKey, O.AutoInc)
    def id_article = column[UUID]("id_article")
    def id_tag = column[UUID]("id_tag")
    def created_at = column[Option[LocalDateTime]]("created_at", O.Default(Some(LocalDateTime.now)))

    override def * = (
      id,
      id_article,
      id_tag,
      created_at,
    ) <> (ArticleTag.tupled, ArticleTag.unapply)
  
  lazy val articleTagTable = TableQuery[ArticleTagTable]
  

