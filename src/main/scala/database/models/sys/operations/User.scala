package database.models.sys.operations

import database.models.sys.User
import database.models.sys.UserTable.userTable

import scala.concurrent.Future
import database.Connection.db
import slick.jdbc.PostgresProfile.api.*

import java.util.UUID


def getUserByUsername(username:String): Future[Option[User]] = db.run(userTable.filter(_.username===username).result.headOption)

def getUserById(id:UUID): Future[Option[User]] = db.run(userTable.filter(_.id===id).result.headOption)
