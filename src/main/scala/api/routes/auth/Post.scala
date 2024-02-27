package api.routes.auth

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}
import api.json.{Auth, AuthJsonSupport}
import api.utils.AuthValidators
import com.password4j.Password
import database.operations.{AuthQ, RefreshTokenQ}
import java.util.UUID
import database.models.RefreshToken
import scala.concurrent.Future
import scala.util.{Failure, Success}


class Post extends Directives with AuthJsonSupport {

  val authOpr = new AuthQ
  val authValidators = new AuthValidators

  val route: Route =
    path("auth") {
      post {
        entity(as[Auth]) { auth =>
          val user = authOpr.getUserByUsername(auth.username)
          if (Password.check(auth.password, user.password).withArgon2()) {
            val infoToken = authValidators.generateAccessToken(user.id, user.admin)
            val refreshToken = RefreshToken(token=UUID.randomUUID().toString, user=auth.username, id=null, revision=null)
            val refreshTokenOpr = new RefreshTokenQ
            val insertNewRefreshToken: Future[Unit] = refreshTokenOpr.addNewRefreshToken(refreshToken)
            onComplete(insertNewRefreshToken) {
              case Success(_) => complete(StatusCodes.OK, Map(
                "auth_token" -> Map("token" -> infoToken._1, "expiration_time" -> infoToken._2),
                "refresh_token" -> Map("refresh_token" -> refreshToken.token, "expiration_time" -> refreshToken.expire)
              ))
              case Failure(exception: Exception) => complete(StatusCodes.InternalServerError, exception.getMessage)
            }
          } else {
            complete(StatusCodes.Unauthorized)
          }
        }
      }
    }
}
