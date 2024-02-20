package api.json

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

case class Auth(
               username:String,
               password:String
               )

trait AuthJsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

  implicit val authFormat: RootJsonFormat[Auth] = jsonFormat2(Auth.apply)

}
