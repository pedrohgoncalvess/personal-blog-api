package database.migration


import com.typesafe.config.{Config, ConfigFactory}
import org.flywaydb.core.Flyway


object Main:

  private val config: Config = ConfigFactory.load()

  private val dbProperties = config.getConfig("postgres").getConfig("properties")
  private val user = dbProperties.getString("user")
  private val password = dbProperties.getString("password")
  private val host = dbProperties.getString("serverName")
  private val port = dbProperties.getInt("portNumber")
  private val dbName = dbProperties.getString("databaseName")

  val flyway = Flyway.configure
  .dataSource(s"jdbc:postgresql://$host:$port/$dbName", user, password)
  .baselineOnMigrate(true)
  .baselineVersion("0")
  .locations("db/migration")
  .load()