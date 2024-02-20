package api.utils

import akka.http.scaladsl.server.directives.Credentials
import database.models.Token
import database.operations.TokenQ
import java.time.format.DateTimeFormatter
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}

class Authentication {

  def verifyToken(token:String): Boolean = {

    val tkn = new TokenQ
    val getToken:Future[Token] = tkn.getTokenByToken(token)
    try {
      val result: Token = Await.result(getToken, 5.seconds)
      result.token == token && java.time.LocalDateTime.parse(result.expire,DateTimeFormatter.ISO_OFFSET_DATE_TIME).isBefore(java.time.LocalDateTime.now)

    } catch {
      case _: Throwable => false
    }
  }

  def myUserPassAuthenticator(credentials: Credentials): Option[Boolean] = {

    credentials match {
      case p@Credentials.Provided(_) if p.provideVerify(verifier=verifyToken) => Some(true)
      case _ => None
    }
  }
}
