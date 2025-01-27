package api.routes.article

import java.util.UUID

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.fasterxml.jackson.annotation.JsonGetter
import org.codehaus.jackson.annotate.JsonSetter
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

import api.utils.JsonFormatters.*


case class UpdateArticle(
                          id:UUID,
                          title:Option[String],
                          description:Option[String],
                          text_pt:Option[String],
                          text_en:Option[String],
                          tags:Option[Array[String]],
                          published:Option[Boolean],
                        )

case class CreateArticle(
                          title: String,
                          description: Option[String],
                          text_pt: Option[String],
                          text_en: Option[String],
                          tags: Array[String],
                          published: Boolean,
                        )

case class DeleteArticle(
                        id: UUID
                        )

trait ManipulateJsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

  implicit val updateFormat: RootJsonFormat[UpdateArticle] = jsonFormat7(UpdateArticle.apply)
  implicit val createFormat: RootJsonFormat[CreateArticle] = jsonFormat6(CreateArticle.apply)
  implicit val deleteFormat: RootJsonFormat[DeleteArticle] = jsonFormat1(DeleteArticle.apply)

}

