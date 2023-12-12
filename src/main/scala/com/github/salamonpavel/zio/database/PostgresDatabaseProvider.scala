package com.github.salamonpavel.zio.database

import com.typesafe.config.{Config, ConfigFactory}
import slick.jdbc.JdbcBackend.Database
import za.co.absa.fadb.slick.SlickPgEngine
import zio._

import scala.concurrent.ExecutionContext.Implicits.global

/**
 *  A class representing a provider of Postgres database.
 */
class PostgresDatabaseProvider(val dbEngine: SlickPgEngine)

object PostgresDatabaseProvider {

  /**
   *  A ZLayer that provides live implementation of PostgresDatabaseProvider.
   */
  val layer: ULayer[PostgresDatabaseProvider] = ZLayer.succeed {
    val config: Config = ConfigFactory.load()
    val dbConfig: Config = config.getConfig("postgres")
    val db: Database = Database.forConfig("", dbConfig)
    val dbEngine = new SlickPgEngine(db)
    new PostgresDatabaseProvider(dbEngine)
  }
}
