package database.models.pub

import java.time.LocalDateTime
import java.util.UUID
import slick.jdbc.PostgresProfile.api._
import ArticleTable._ 


case class TagT(
                id: Option[UUID],
                id_article: UUID,
                name: String,
                created_at: Option[LocalDateTime] = Some(LocalDateTime.now)
              )


object TagT:
  def tupled = (TagT.apply _).tupled


object TagTable:

  import slick.jdbc.PostgresProfile.api._
  
  class TagTable(tag: Tag) extends Table[TagT](tag, Some("pub"), "tag") {
    def id = column[Option[UUID]]("id", O.PrimaryKey, O.AutoInc)
    def id_article = column[UUID]("id_article")
    def name = column[String]("name", O.Length(30))
    def created_at = column[Option[LocalDateTime]]("created_at", O.Default(Some(LocalDateTime.now)))
    
    def articleFk = foreignKey(
      "tag_article_fk",
      id_article,
      articleTable
    )(_.id.get,
      onUpdate=ForeignKeyAction.Restrict,
      onDelete=ForeignKeyAction.Cascade
    )

    override def * = (
      id,
      id_article,
      name,
      created_at
    ) <> (TagT.tupled, TagT.unapply)
  }
  
  lazy val tagTable = TableQuery[TagTable]
