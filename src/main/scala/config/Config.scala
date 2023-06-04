package config

import com.typesafe.config.ConfigFactory
import pureconfig._
import pureconfig.generic.auto._

case class ServerConfig(host: String, port: Int)
object ServerConfig {
  def load(): ServerConfig = {
    val config = ConfigFactory.load().getConfig("server")
    ConfigSource.fromConfig(config).loadOrThrow[ServerConfig]
  }
}

case class DatabaseConfig(host: String, port: Int)
object DatabaseConfig {
  def load(): DatabaseConfig = {
    val config = ConfigFactory.load().getConfig("database")
    ConfigSource.fromConfig(config).loadOrThrow[DatabaseConfig]
  }
}


