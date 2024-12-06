package api.routes.article

import java.time.LocalDateTime
import java.util.UUID

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

import api.utils.JsonFormatters.*


case class ArticlePreview(
                   id: UUID,
                   title: String,
                   description: Option[String],
                   tags: Array[String],
                   created_at: LocalDateTime,
                   )

case class ArticlesPreview(
                   articles: Array[ArticlePreview]
                   )

case class ArticleComplete(
                          id: UUID,
                          title: String,
                          description: Option[String],
                          tags: Array[String],
                          text_pt: Option[String],
                          text_en: String,
                          author: String,
                          created_at: LocalDateTime,
                          updated_at: Option[LocalDateTime],
                          published: Boolean,
                          published_at: Option[LocalDateTime]
                          )

trait GetJsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

  implicit val articlePreviewFormat: RootJsonFormat[ArticlePreview] = jsonFormat5(ArticlePreview.apply)
  implicit val articlesPreviewFormat: RootJsonFormat[ArticlesPreview] = jsonFormat1(ArticlesPreview.apply)
  implicit val articlesCompleteFormat: RootJsonFormat[ArticleComplete] = jsonFormat11(ArticleComplete.apply)

}