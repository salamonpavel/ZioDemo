package com.github.salamonpavel.zio.database

import com.typesafe.config
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
  val layer: RLayer[config.Config, PostgresDatabaseProvider] = ZLayer {
    for {
      config <- ZIO.service[config.Config]
      db <- ZIO.succeed(Database.forConfig("", config.getConfig("postgres")))
      dbEngine <- ZIO.succeed(new SlickPgEngine(db))
    } yield new PostgresDatabaseProvider(dbEngine)
  }
}
