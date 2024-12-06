package api.routes.auth


import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, RootJsonFormat}
import spray.json._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import DefaultJsonProtocol._

import java.time.LocalDateTime
import java.util.UUID

import api.utils.JsonFormatters.*


case class Credentials(
               username:String,
               password:String
               )


case class AuthJson(
               auth_token: AuthTokenJson,
               refresh_token: RefreshTokenJson
               )

case class AuthTokenJson(
                    token: String,
                    expire_at: LocalDateTime
                    ) 

case class RefreshTokenJson(
                           refresh_token: UUID,
                           expire_at: LocalDateTime
                       )

trait AuthJsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  
  implicit val credentialsFormat: RootJsonFormat[Credentials] = jsonFormat2(Credentials.apply)
  implicit val authFormat: RootJsonFormat[AuthJson] = jsonFormat2(AuthJson.apply)
  implicit val authToken: RootJsonFormat[AuthTokenJson] = jsonFormat2(AuthTokenJson.apply)
  implicit val refreshToken: RootJsonFormat[RefreshTokenJson] = jsonFormat2(RefreshTokenJson.apply)
  
}
