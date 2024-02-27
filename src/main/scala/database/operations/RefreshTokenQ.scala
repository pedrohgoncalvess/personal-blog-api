package database.operations

import database.CouchConnection
import database.models.{RefreshToken, TokenRepository}
import scala.concurrent.Future

class RefreshTokenQ extends CouchConnection {

  import scala.concurrent.ExecutionContext.Implicits.global

  def addNewRefreshToken(token:RefreshToken): Future[Unit] = {
    Future {
      couchInstance { db =>
        try {
          db.create(token)
          Future.successful()
        } catch {
          case ex: Throwable =>
            Future.failed(throw new Exception(ex.getMessage))
        }
      }
    }
  }

  def getRefreshTokenByToken(token:String): Future[RefreshToken] = {
    Future {
      couchInstance{ db =>
        val tokenRepo = new TokenRepository(db)
        tokenRepo.findByToken(token)
      }
    }
  }
}
