package database.models.sys.operations

import database.models.sys.RefreshToken
import database.models.sys.RefreshTokenTable.refreshTokenTable
import database.Connection.db

import scala.concurrent.Future
import slick.jdbc.PostgresProfile.api.*

import java.util.UUID


def insertRefreshToken(token: RefreshToken): Future[Int] = db.run(refreshTokenTable += token)

def getRefreshTokenByToken(token: UUID): Future[Option[RefreshToken]] = db.run(refreshTokenTable.filter(_.token===token).result.headOption)