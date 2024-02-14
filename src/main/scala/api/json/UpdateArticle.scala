package api.json

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

case class UpdateArticle(id:String,
                         name:Option[String],
                         description:Option[String],
                         text_pt:Option[String],
                         text_en:Option[String],
                         tags:Option[Array[String]],
                         is_published:Boolean,
                        )

trait UpdateArticleJsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

  implicit val articleToUpdateFormat: RootJsonFormat[UpdateArticle] = jsonFormat7(UpdateArticle.apply)

}