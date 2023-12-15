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
//  val layer: ULayer[PostgresDatabaseProvider] = ZLayer.succeed {
//    val config: Config = ConfigFactory.load()
//    val dbConfig: Config = config.getConfig("postgres")
//    val db: Database = Database.forConfig("", dbConfig)
//    val dbEngine = new SlickPgEngine(db)
//    new PostgresDatabaseProvider(dbEngine)
//  }

//  val layer = ZLayer {
//    for {
//      postgresConfig <- ZIO.config[PostgresConfig](PostgresConfig.config)
//      dbConfig = ConfigFactory.parseMap(
//        Map[String, Any](
//          "connectionPool" -> postgresConfig.connectionPool,
//          "dataSourceClass" -> postgresConfig.dataSourceClass,
//          "numThreads" -> postgresConfig.numThreads,
//          "properties.serverName" -> postgresConfig.properties.serverName,
//          "properties.portNumber" -> postgresConfig.properties.portNumber,
//          "properties.databaseName" -> postgresConfig.properties.databaseName,
//          "properties.user" -> postgresConfig.properties.user,
//          "properties.password" -> postgresConfig.properties.password,
//        ).asJava
//      )
//      db = Database.forConfig("", dbConfig)
//      dbEngine = new SlickPgEngine(db)
//    } yield new PostgresDatabaseProvider(dbEngine)
//  }

  val layer: RLayer[config.Config, PostgresDatabaseProvider] = ZLayer {
    for {
      config <- ZIO.service[config.Config]
      db <- ZIO.succeed(Database.forConfig("", config.getConfig("postgres")))
      dbEngine <- ZIO.succeed(new SlickPgEngine(db))
    } yield new PostgresDatabaseProvider(dbEngine)
  }
}
