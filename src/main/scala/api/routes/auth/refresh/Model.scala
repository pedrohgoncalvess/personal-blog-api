package api.routes.auth.refresh

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, RootJsonFormat}
import java.util.UUID
import api.utils.JsonFormatters.*


case class RefreshTokenJson(
                       token: UUID
                       )

trait RefreshTokenJsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

  implicit val refreshTokenFormat: RootJsonFormat[RefreshTokenJson] = jsonFormat1(RefreshTokenJson.apply)

}
