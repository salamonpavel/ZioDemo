package com.github.salamonpavel.zio.database

import com.typesafe.config.{Config, ConfigFactory}
import slick.jdbc.JdbcBackend.Database
import za.co.absa.fadb.slick.SlickPgEngine
import zio.{ULayer, ZLayer}

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * A class representing a provider of a Postgres database.
 */
class PostgresDatabaseProvider {
  val config: Config = ConfigFactory.parseResources("application.properties")
  val dbConfig: Config = config.getConfig("postgres")
  val db: Database = Database.forConfig("", dbConfig)
  val dbEngine = new SlickPgEngine(db)
}

object PostgresDatabaseProvider {
  /**
   * A ZLayer that provides live implementation of PostgresDatabaseProvider.
   */
  val live: ULayer[PostgresDatabaseProvider] = ZLayer.succeed(new PostgresDatabaseProvider)
}