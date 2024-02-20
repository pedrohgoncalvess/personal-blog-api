package api.json

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.fasterxml.jackson.annotation.JsonGetter
import org.codehaus.jackson.annotate.JsonSetter
import spray.json.DefaultJsonProtocol
import spray.json.RootJsonFormat

trait CreateArticleJsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

  implicit val createArticleFormat: RootJsonFormat[CreateArticle] = jsonFormat9(CreateArticle.apply)
}

case class CreateArticle(
                         var id: Option[String],
                         var revision: Option[String],
                         var name: String,
                         var description: String,
                         var text_pt: Option[String],
                         var text_en: Option[String],
                         var tags:Array[String],
                         var published:Boolean,
                         var updated_at: Option[String]
                        ) {

  @JsonGetter("_id") def getId: Option[String] = id

  @JsonGetter("_rev") def getRevision: Option[String] = revision

  @JsonSetter("_rev") def setRevision(s: String): Unit = revision = Some(s)

  @JsonSetter("_id") def setId(s: String): Unit = id = Some(s)

  @JsonSetter def setName(s: String): Unit = name = s

  @JsonSetter def setDescription(s: String): Unit = description = s

  @JsonSetter def setText_pt(s: String): Unit = text_pt = Some(s)

  @JsonSetter def setText_en(s: String): Unit = text_en = Some(s)

  @JsonSetter def setTags(s: Array[String]): Unit = tags = s

  @JsonSetter def setPublished(s: Boolean): Unit = published = s

  @JsonSetter def setUpdated_at(s: String): Unit = updated_at = Some(s)

  @JsonGetter def getName: String = name

  @JsonGetter def getDescription: String = description

  @JsonGetter def getText_pt: String = text_pt.get

  @JsonGetter def getText_en: String = text_en.get

  @JsonGetter def getTags: Array[String] = tags

  @JsonGetter def getPublished: Boolean = published

  @JsonGetter def getUpdated_at: String = updated_at.get

  def this() = this(Some(""), Some(""), "", "", None, None, Array.empty, false, Some(""))
}

