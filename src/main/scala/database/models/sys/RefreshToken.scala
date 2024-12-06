package database.models.sys

import database.models.sys.UserTable.userTable

import java.time.LocalDateTime
import java.util.UUID


case class RefreshToken(
                 id: Option[UUID],
                 id_user: UUID,
                 token: UUID,
                 status: Boolean = true,
                 created_at: Option[LocalDateTime] = Some(LocalDateTime.now)
               )


object RefreshToken:
  def tupled = (RefreshToken.apply _).tupled


object RefreshTokenTable:

  import slick.jdbc.PostgresProfile.api._

  class RefreshTokenTable(tag: Tag) extends Table[RefreshToken](tag, Some("sys"), "refresh_token"):
    def id = column[Option[UUID]]("id", O.PrimaryKey, O.Default(Some(UUID.randomUUID())))
    def id_user = column[UUID]("id_user")
    def token = column[UUID]("token", O.Unique)
    def status = column[Boolean]("status", O.Default(true))
    def created_at = column[Option[LocalDateTime]]("created_at", O.Default(Some(LocalDateTime.now)))

    def userFk = foreignKey(
        "refresh_token_user_fk",
        id_user,
        userTable
      )(_.id.get,
        onUpdate = ForeignKeyAction.Cascade,
        onDelete = ForeignKeyAction.Cascade
      )

    override def * = (
      id,
      id_user,
      token,
      status,
      created_at
    ) <> (RefreshToken.tupled, RefreshToken.unapply)

  lazy val refreshTokenTable = TableQuery[RefreshTokenTable]
