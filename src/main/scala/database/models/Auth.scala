package api.models

import com.fasterxml.jackson.annotation.{JsonGetter, JsonIgnoreProperties}
import org.ektorp.CouchDbConnector
import org.ektorp.support.{CouchDbRepositorySupport, GenerateView, TypeDiscriminator, View}
import org.codehaus.jackson.annotate.{JsonProperty, JsonSetter}


@JsonIgnoreProperties(ignoreUnknown=true)
@TypeDiscriminator("doc.type == 'UserAuth'")
@View(name = "by_username", map = "function(doc) {if (doc.username) {emit(doc.username, doc);}}")
case class User(
                 @JsonProperty("_id") id: String,
                 @JsonProperty("_rev") revision: String,
                 @JsonProperty var username: String,
                 @JsonProperty var email: String,
                 @JsonProperty var password: String,
                 @JsonProperty var admin:Boolean,
               ) {
  @JsonGetter("_id") def getId: String = id
  @JsonSetter("_id") def setId(id: String): Unit = id

  @JsonGetter("_rev") def getRevision: String = revision
  @JsonSetter("_rev") def setRevision(rev: String): Unit = rev

  @JsonGetter def getUsername: String = username
  @JsonSetter def setUsername(newUsername: String): Unit = username = newUsername

  @JsonGetter def getEmail: String = email
  @JsonSetter def setEmail(newEmail: String): Unit = email = newEmail

  @JsonGetter def getPassword: String = password
  @JsonSetter def setPassword(s: String): Unit = password = s

  @JsonGetter def getAdmin:Boolean = admin
  @JsonSetter def setAdmin(s: Boolean): Unit = admin = s

  def this() = this("", "", "", "", "", false)
}

class UserRepository(db: CouchDbConnector) extends CouchDbRepositorySupport[User](classOf[User], db) {
  initStandardDesignDocument()

  @GenerateView def findByUsername(username: String): User = queryView("by_username", username).get(0)
}