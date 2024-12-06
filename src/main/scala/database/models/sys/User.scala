package database.models.sys

import java.time.LocalDateTime
import java.util.UUID

case class User(
               id: Option[UUID],
               username: String,
               name: String,
               password: Array[Byte],
               admin: Boolean,
               status: Boolean,
               created_at: Option[LocalDateTime]
               )


object User {
  def tupled = (User.apply _).tupled
}


object UserTable {

  import slick.jdbc.PostgresProfile.api._

  class UserTable(tag: Tag) extends Table[User](tag, Some("sys"), "user") {
    def id = column[Option[UUID]]("id", O.PrimaryKey)
    def username = column[String]("username", O.Unique, O.Length(30))
    def name = column[String]("name", O.Length(70))
    def admin = column[Boolean]("admin", O.Default(false))
    def password = column[Array[Byte]]("password")
    def status = column[Boolean]("status", O.Default(true))
    def created_at = column[Option[LocalDateTime]]("created_at", O.Default(Some(LocalDateTime.now)))

    override def * = (
      id,
      username,
      name,
      password,
      admin,
      status,
      created_at
    ) <> (User.tupled, User.unapply)
  }

  lazy val userTable = TableQuery[UserTable]
}
