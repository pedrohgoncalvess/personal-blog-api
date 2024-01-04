package database

import com.typesafe.config.{Config, ConfigFactory}
import org.ektorp.CouchDbConnector
import org.ektorp.http.StdHttpClient
import org.ektorp.impl.StdCouchDbInstance


class CouchConnection {

  private val config: Config = ConfigFactory.load()
  private val dbName = config.getString("DB_NAME")
  private val dbUrl = s"http://${config.getString("DB_HOST")}:${config.getInt("DB_PORT")}"
  val httpClient = new StdHttpClient.Builder().url(dbUrl).username(config.getString("DB_USERNAME")).password(config.getString("DB_PASSWORD")).build()
  val dbInstance = new StdCouchDbInstance(httpClient)


  def couchInstance[T](operation: CouchDbConnector => T): T = {
    val httpClient = new StdHttpClient.Builder().url(dbUrl).username(config.getString("DB_USERNAME")).password(config.getString("DB_PASSWORD")).build()
    val dbInstance = new StdCouchDbInstance(httpClient)

    val db = dbInstance.createConnector(dbName, false)

    try {
      operation(db)
    }
  }
}