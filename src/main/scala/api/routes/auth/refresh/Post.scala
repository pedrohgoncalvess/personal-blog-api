package api.routes.auth.refresh


import java.util.UUID
import scala.util.{Failure, Success}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}
import api.routes.auth.{AuthJsonSupport, AuthTokenJson, RefreshTokenJson as RefreshTokenJsonReturn}
import api.utils.AuthValidators
import api.utils.AuthValidators.generateAccessToken
import database.models.sys.RefreshToken
import database.models.sys.operations.{getRefreshTokenByToken, getUserById, insertRefreshToken}
import spray.json.*


class Post extends Directives with RefreshTokenJsonSupport with AuthJsonSupport:

  val route: Route = {
    pathPrefix("auth") {
      path("refresh") {
        post {
          entity(as[RefreshTokenJson]) { tokenRequest =>
            val validateToken = getRefreshTokenByToken(tokenRequest.token)
            onComplete(validateToken) {
              case Success(possToken) =>
                if (possToken.orNull == null)
                  complete(StatusCodes.Unauthorized)
                else
                  val token = possToken.get
                  if (token.created_at.get.plusHours(5).isAfter(java.time.LocalDateTime.now))
                    val userOpr = getUserById(token.id_user)
                    onComplete(userOpr) {
                      case Success(userOp) =>
                        val user = userOp.get
                        val newToken = generateAccessToken(userId = token.id_user, admin=user.admin)
                        val refreshToken = RefreshToken(id = Some(null), token = UUID.randomUUID(), id_user = token.id_user)
                        val insertNewRefreshToken = insertRefreshToken(refreshToken)
                        onComplete(insertNewRefreshToken) {
                          case Success(_) =>
                            val authToken = AuthTokenJson(newToken._1, newToken._2)
                            val retRefreshToken = RefreshTokenJsonReturn(refreshToken.token, refreshToken.created_at.get.plusHours(5))
                            complete(
                              StatusCodes.Created,
                              JsObject(
                                "auth" -> authToken.toJson,
                                "refresh" -> retRefreshToken.toJson
                              )
                            )
                          case Failure(_) => complete(StatusCodes.InternalServerError, "An error occurred.")
                        }
                      case Failure(exception: Exception) => complete(StatusCodes.InternalServerError, "An error occurred.")
                    }
                  else 
                    complete(StatusCodes.Unauthorized, "Not valid refresh token ")
              case Failure(_) => complete(StatusCodes.InternalServerError, "An error occurred.")
            }
          }
        }
      }
    }
  }
