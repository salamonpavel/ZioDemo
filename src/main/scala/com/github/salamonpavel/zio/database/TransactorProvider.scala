package com.github.salamonpavel.zio.database

import com.github.salamonpavel.zio.config.PostgresConfig
import com.zaxxer.hikari.HikariConfig
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import zio._
import zio.interop.catz._

object TransactorProvider {

  val layer: ZLayer[Any with Scope, Throwable, HikariTransactor[Task]] = ZLayer {
    val transactor = for {
      postgresConfig <- ZIO.config[PostgresConfig](PostgresConfig.config)
      ec             <- ExecutionContexts.fixedThreadPool[Task](postgresConfig.numThreads).toScopedZIO
      hikariConfig = {
        val config = new HikariConfig()
        config.setDriverClassName(postgresConfig.dataSourceClass)
        config.setJdbcUrl(
          s"jdbc:postgresql://${postgresConfig.serverName}:${postgresConfig.portNumber}/${postgresConfig.databaseName}"
        )
        config.setUsername(postgresConfig.user)
        config.setPassword(postgresConfig.password)
        config
      }

      xa <- HikariTransactor.fromHikariConfig[Task](hikariConfig, ec).toScopedZIO
    } yield xa

    transactor
  }
}
