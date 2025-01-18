package database.models.pub


import java.time.LocalDateTime
import java.util.UUID

import slick.jdbc.PostgresProfile.api._

import ArticleTable._ 


/*is called TagT instead of Tag because of a Slick dependency Tag object*/
case class TagT(
                id: Option[UUID],
                name: String,
                created_at: Option[LocalDateTime] = Some(LocalDateTime.now)
              )


object TagT:
  def tupled = (TagT.apply).tupled


object TagTable:
  
  class TagTable(tag: Tag) extends Table[TagT](tag, Some("pub"), "tag") {
    def id = column[Option[UUID]]("id", O.PrimaryKey)
    def name = column[String]("name", O.Length(30), O.Unique)
    def created_at = column[Option[LocalDateTime]]("created_at", O.Default(Some(LocalDateTime.now)))

    override def * = (
      id,
      name,
      created_at
    ) <> (TagT.tupled, TagT.unapply)
  }
  
  lazy val tagTable = TableQuery[TagTable]
