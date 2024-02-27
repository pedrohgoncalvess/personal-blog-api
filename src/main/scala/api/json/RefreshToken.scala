package api.json

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

case class RefreshTokenJson(
                       token: String
                       )

trait RefreshTokenJsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

  implicit val refreshTokenFormat: RootJsonFormat[RefreshTokenJson] = jsonFormat1(RefreshTokenJson.apply)

}
