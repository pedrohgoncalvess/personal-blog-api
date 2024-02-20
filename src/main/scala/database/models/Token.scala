package database.models

import com.fasterxml.jackson.annotation.JsonGetter
import org.codehaus.jackson.annotate.{JsonIgnoreProperties, JsonProperty, JsonSetter}
import org.ektorp.CouchDbConnector
import org.ektorp.support.{CouchDbRepositorySupport, GenerateView, View}

@JsonIgnoreProperties(ignoreUnknown=true)
@View(name = "by_token", map = "function(doc) {if (doc.token) {emit(doc.token, doc);}}")
case class Token(
                @JsonProperty("_id") var id: Option[String],
                @JsonProperty("_rev") var revision: Option[String],
                @JsonProperty var token:String,
                @JsonProperty var user:String,
                @JsonProperty var created_at:String = java.time.LocalDateTime.now().toString,
                @JsonProperty var expire:String = java.time.LocalDateTime.now().plusMinutes(120).toString
                ) {
  @JsonGetter("_id") def getId: Option[String] = id

  @JsonGetter("_rev") def getRevision: Option[String] = revision

  @JsonSetter("_rev") def setRevision(s: String): Unit = revision = Some(s)

  @JsonSetter("_id") def setId(s: String): Unit = id = Some(s)

  @JsonGetter def getToken: String = token
  @JsonGetter def getUser:String = user
  @JsonGetter def getCreated_at:String = created_at
  @JsonGetter def getExpire:String = expire
  @JsonSetter def setToken(s:String):Unit = token = s
  @JsonSetter def setUser(s:String):Unit = user = s
  @JsonSetter def setExpire(s:String): Unit = expire = s

  def this() = this(Some(""),Some(""),"","","","")
}

class TokenRepository(db: CouchDbConnector) extends CouchDbRepositorySupport[Token](classOf[Token], db) {
  initStandardDesignDocument()

  @GenerateView def findByToken(token: String): Token = queryView("by_token", token).get(0)
}
