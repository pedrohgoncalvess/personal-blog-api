package database.operations

import database.CouchConnection
import database.models.{Token, TokenRepository}

import scala.concurrent.Future

class TokenQ extends CouchConnection {

  import scala.concurrent.ExecutionContext.Implicits.global

  def addNewToken(token:Token): Future[Unit] = {
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

  def getTokenByToken(token:String): Future[Token] = {
    Future {
      couchInstance{ db =>
        val tokenRepo = new TokenRepository(db)
        tokenRepo.findByToken(token)
      }
    }
  }
}
