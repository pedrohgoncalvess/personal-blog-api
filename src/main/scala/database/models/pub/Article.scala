package database.models.pub

import java.time.LocalDateTime
import java.util.UUID
import slick.jdbc.PostgresProfile.api._


case class Article(
                    id: Option[UUID],
                    id_user: UUID,
                    title: String,
                    description: Option[String],
                    text_pt: Option[String],
                    text_en: String,
                    published: Boolean = false,
                    created_at: Option[LocalDateTime] = Some(LocalDateTime.now),
                    updated_at: Option[LocalDateTime],
                    published_at: Option[LocalDateTime]
                  )


object Article:
  def tupled = (Article.apply _).tupled


object ArticleTable:
  
  class ArticleTable(tag: Tag) extends Table[Article](tag, Some("pub"), "article"):
    def id = column[Option[UUID]]("id", O.PrimaryKey, O.AutoInc)
    def id_user = column[UUID]("id_user")
    def title = column[String]("title")
    def description = column[Option[String]]("description", O.Length(350))
    def text_pt = column[Option[String]]("text_pt")
    def text_en = column[String]("text_en")
    def published = column[Boolean]("published", O.Default(false))
    def created_at = column[Option[LocalDateTime]]("created_at", O.Default(Some(LocalDateTime.now)))
    def updated_at = column[Option[LocalDateTime]]("updated_at")
    def published_at = column[Option[LocalDateTime]]("published_at")

    override def * = (
      id,
      id_user,
      title,
      description,
      text_pt,
      text_en,
      published,
      created_at,
      updated_at,
      published_at
    ) <> (Article.tupled, Article.unapply)
  
  lazy val articleTable = TableQuery[ArticleTable]
  

