package database.models

import com.fasterxml.jackson.annotation.JsonGetter
import org.codehaus.jackson.annotate.{JsonIgnoreProperties, JsonProperty, JsonSetter}
import org.ektorp.CouchDbConnector
import org.ektorp.support.{CouchDbRepositorySupport, GenerateView, View}

@JsonIgnoreProperties(ignoreUnknown=true)
@View(name = "by_token", map = "function(doc) {if (doc.token) {emit(doc.token, doc);}}")
case class RefreshToken(
                 var id: Option[String],
                 var revision: Option[String],
                 var token:String,
                 var user:String,
                 var created_at:String = java.time.LocalDateTime.now().toString,
                 var expire:String = java.time.LocalDateTime.now().plusMinutes(360).toString
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

class TokenRepository(db: CouchDbConnector) extends CouchDbRepositorySupport[RefreshToken](classOf[RefreshToken], db) {
  initStandardDesignDocument()

  @GenerateView def findByToken(token: String): RefreshToken = queryView("by_token", token).get(0)
}
