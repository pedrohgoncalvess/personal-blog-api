package api.routes.auth

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives
import api.json.{Auth, AuthJsonSupport}
import api.utils.Authentication
import com.password4j.Password
import database.models.Token
import database.operations.{AuthQ, TokenQ}
import java.util.UUID
import scala.concurrent.Future
import scala.util.{Failure, Success}


class Post extends Directives with AuthJsonSupport {

  val auth = new Authentication
  val token = new TokenQ
  val opr = new AuthQ

  val route =
    path("auth") {
      post {
        entity(as[Auth]) { auth =>
          val user = opr.getUserByUsername(auth.username)
          if (Password.check(auth.password, user.password).withArgon2()) {
            val tokenString = UUID.randomUUID().toString
            val newToken = Token(id=null,revision=null,token=tokenString, user=auth.username)
            val insertToken: Future[Unit] = token.addNewToken(newToken)
            onComplete(insertToken) {
              case Success(_) => complete(StatusCodes.OK, Map("token" -> tokenString, "expire" -> newToken.expire))
              case Failure(exception) =>
                println(exception)
                complete(StatusCodes.InternalServerError, exception.toString)
            }
          } else {
            complete(StatusCodes.Unauthorized)
          }

        }
      }
    }

}
