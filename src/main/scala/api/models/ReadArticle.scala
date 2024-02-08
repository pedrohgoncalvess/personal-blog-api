package api.models

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, RootJsonFormat}
import com.fasterxml.jackson.annotation.{JsonGetter, JsonIgnoreProperties, JsonRawValue}
import org.codehaus.jackson.annotate.{JsonProperty, JsonSetter}
import org.ektorp.CouchDbConnector
import org.ektorp.support.{CouchDbRepositorySupport, GenerateView, TypeDiscriminator, View, Views}


@JsonIgnoreProperties(ignoreUnknown=true)
@TypeDiscriminator("doc.type == 'UserAuth'")
@Views(
  Array(
    new View(name="by_name", map="function(doc) {if (doc.name) {emit(doc.name.toLowerCase(), doc);}}"),
    new View(name="by_is_published", map="function(doc) {if (doc.published != null) {emit(doc.published, doc);}}"),
    new View(name="by_tag", map="function(doc) {if (doc.tags) {doc.tags.forEach(function(tag) {emit(tag.toLowerCase(), doc);});}}")
  )
)
case class Article(@JsonProperty var id: Option[String],
                   @JsonProperty var revision: Option[String],
                   @JsonProperty var name: String,
                   @JsonProperty var description: String,
                   @JsonProperty var text_pt: Option[String],
                   @JsonProperty var text_en: Option[String],
                   @JsonRawValue var tags:Array[String],
                   @JsonProperty var published: Boolean,
                   @JsonProperty var updated_at: Option[String]
                  ) {

  @JsonGetter("_id")  def getId: String = if (id == null) "" else id.get //to create and delete get_id and get_rev cannot be null
  @JsonGetter("_rev") def getRevision: String = if (revision == null) "" else revision.get
  @JsonSetter("_rev") def setRevision(s: String): Unit = revision = Some(s)
  @JsonSetter("_id") def setId(s:String):Unit = id = Some(s)

  @JsonSetter def setName(s: String): Unit = name = s

  @JsonSetter def setDescription(s: String): Unit = description = s

  @JsonSetter def setText_pt(s: String): Unit = text_pt = Some(s)

  @JsonSetter def setText_en(s: String): Unit = text_en = Some(s)

  @JsonSetter def setTags(s: Array[String]):Unit = tags = s

  @JsonSetter def setPublished(s: Boolean):Unit = published = s

  @JsonSetter def setUpdated_at(s: String): Unit = updated_at = Some(s)

  @JsonGetter def getName: String = name
  @JsonGetter def getDescription: String = description
  @JsonGetter def getText_pt: String = text_pt.get
  @JsonGetter def getText_en: String = text_en.get
  @JsonGetter def getTags:Array[String] = tags
  @JsonGetter def getPublished: Boolean = published
  @JsonGetter def getUpdated_at: String = updated_at.get

  def this() = this(Some(""), Some(""), "", "", None, None, Array.empty, false, Some(""))

}

class ArticleRepository(db: CouchDbConnector) extends CouchDbRepositorySupport[Article](classOf[Article], db) {
  initStandardDesignDocument()

  @GenerateView def findByTag(tag: String): java.util.List[Article] = queryView("by_tag", tag.toLowerCase())
  @GenerateView def findByPublished: java.util.List[Article] = queryView("by_is_published","true")

}

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {

  implicit val articleFormat: RootJsonFormat[Article] = jsonFormat9(Article.apply)
  implicit val orderFormat: RootJsonFormat[Articles] = jsonFormat1(Articles.apply)
}

final case class Articles(articles: List[Article])