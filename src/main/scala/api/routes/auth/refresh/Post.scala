package api.routes.auth.refresh

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}
import api.json.{RefreshTokenJson, RefreshTokenJsonSupport}
import api.utils.AuthValidators
import database.models.RefreshToken
import database.operations.{AuthQ, RefreshTokenQ}
import java.time.format.DateTimeFormatter
import java.util.UUID
import scala.concurrent.Future
import scala.util.{Failure, Success}

/*
NEED TO REFACTOR PERMISSION
*/
class Post extends Directives with RefreshTokenJsonSupport {

  val tokenOpr = new RefreshTokenQ
  val authOpr = new AuthQ
  val authValidators = new AuthValidators
  val refreshTokenOpr = new RefreshTokenQ

  val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS")

  val route: Route = {
    pathPrefix("auth") {
      path("refresh") {
        post {
          entity(as[RefreshTokenJson]) { tokenRequest =>
            val validadeToken: Future[RefreshToken] = tokenOpr.getRefreshTokenByToken(tokenRequest.token)
            onComplete(validadeToken) {
              case Success(token) =>
                if (java.time.LocalDateTime.parse(token.expire, formatter).isAfter(java.time.LocalDateTime.now)) {
                  val newToken = authValidators.generateAccessToken(userId = token.user, admin = true) //<- refac permission getting user in database
                  val refreshToken = RefreshToken(token = UUID.randomUUID().toString, user = token.user, id = null, revision = null)
                  val insertNewRefreshToken: Future[Unit] = refreshTokenOpr.addNewRefreshToken(refreshToken)
                  onComplete(insertNewRefreshToken) {
                    case Success(_) => complete(StatusCodes.OK, Map(
                      "auth_token" -> Map("token" -> newToken._1, "expiration_time" -> newToken._2),
                      "refresh_token" -> Map("refresh_token" -> refreshToken.token, "expiration_time" -> refreshToken.expire)
                    ))
                    case Failure(exception: Exception) => complete(StatusCodes.InternalServerError, exception.getMessage)
                  }
                } else {
                  complete(StatusCodes.Unauthorized, "Authenticate on the /auth route")
                }
              case Failure(exception: Exception) => complete(StatusCodes.InternalServerError, exception.getMessage)
            }
          }
        }
      }
    }
  }
}
