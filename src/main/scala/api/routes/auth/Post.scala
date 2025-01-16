package api.routes.auth


import java.time.LocalDateTime
import scala.util.{Failure, Success}

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}
import com.password4j.Password
import spray.json.*
import java.util.{Base64, UUID}

import api.utils.AuthValidators
import api.utils.AuthValidators.generateAccessToken
import database.models.sys.{RefreshToken, User}
import database.models.sys.operations.{getUserByUsername, insertRefreshToken}


class Post extends Directives with AuthJsonSupport:

  private def decodeBase64(encoded: String): String =
    val decoder = Base64.getDecoder
    val decodedBytes = decoder.decode(encoded)
    new String(decodedBytes, "UTF-8")

  val route: Route =
    path("auth") {
      post {
        entity(as[Credentials]) { auth =>
          val userOp = getUserByUsername(auth.username)
          onComplete(userOp) {
            case Success(possUser) =>
              if (possUser.orNull == null)
                complete(StatusCodes.Unauthorized)
              else
                val user: User = possUser.get
                val userPassDecoded:String = decodeBase64(auth.password)
                if (Password.check(userPassDecoded, new String(user.password)).withArgon2()) {
                  val newToken = generateAccessToken(user.id.get, user.admin)
                  val refreshToken = RefreshToken(id=Some(UUID.randomUUID()), token=UUID.randomUUID(), id_user=user.id.get, created_at=Some(LocalDateTime.now))
                  val insertToken = insertRefreshToken(refreshToken)
                  onComplete(insertToken) {
                    case Success(_) =>
                      val authToken = AuthTokenJson(newToken._1, newToken._2)
                      val retRefreshToken = RefreshTokenJson(refreshToken.token, refreshToken.created_at.get.plusHours(5))
                      complete(
                        StatusCodes.Created,
                        JsObject(
                          "auth" -> authToken.toJson,
                          "refresh" -> retRefreshToken.toJson
                        )
                      )

                    case Failure(_) => complete(StatusCodes.InternalServerError, "An error occurred.")
                  }
                } else {
                  complete(StatusCodes.Unauthorized)
                }
            case Failure(_) => complete(StatusCodes.InternalServerError, "An error ocurred.")
          }
        }
      }
    }
