package com.github.salamonpavel.zio.database

import doobie.Transactor
import za.co.absa.fadb.doobiedb.DoobieEngine
import zio._
import zio.interop.catz._

/**
 *  A class representing a provider of Postgres database.
 */
class PostgresDatabaseProvider(val dbEngine: DoobieEngine[Task])

object PostgresDatabaseProvider {

  /**
   *  A ZLayer that provides live implementation of PostgresDatabaseProvider.
   */
//  val layer = ZLayer {
//    for {
//      transactor <- ZIO.succeed(
//      for {
//        hikariConfig <- Resource.pure {
//          val config = new HikariConfig()
//          config.setDataSourceClassName("org.postgresql.ds.PGSimpleDataSource")
//          config.setJdbcUrl("some url")
//          config.setUsername("user")
//          config.setPassword("password")
//          config.setMaximumPoolSize(10)
//          config
//        }
//        xa <- HikariTransactor.fromHikariConfig[Task](hikariConfig)
//      } yield xa
//      )
//    } yield new PostgresDatabaseProvider(transactor)
//  }

    val layer: ULayer[PostgresDatabaseProvider] = ZLayer {
      for {
        transactor <- ZIO.succeed(
          Transactor.fromDriverManager[Task] (
            driver = "org.postgresql.Driver",
            url = "jdbc:postgresql://localhost:5432/movies",
            user = "postgres",
            password = "postgres",
            None
          )
        )
        doobieEngine <- ZIO.succeed(new DoobieEngine[Task](transactor))
      } yield new PostgresDatabaseProvider(doobieEngine)
  }
}
