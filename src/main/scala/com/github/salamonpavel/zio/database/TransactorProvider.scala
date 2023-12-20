package com.github.salamonpavel.zio.database

import com.github.salamonpavel.zio.config.PostgresConfig
import com.zaxxer.hikari.HikariConfig
import doobie.hikari.HikariTransactor
import zio.Runtime.defaultBlockingExecutor
import zio._
import zio.interop.catz._

object TransactorProvider {

  val layer: ZLayer[Any with Scope, Throwable, HikariTransactor[Task]] = ZLayer {
    for {
      postgresConfig <- ZIO.config[PostgresConfig](PostgresConfig.config)
      hikariConfig = {
        val config = new HikariConfig()
        config.setDriverClassName(postgresConfig.dataSourceClass)
        config.setJdbcUrl(
          s"jdbc:postgresql://${postgresConfig.serverName}:${postgresConfig.portNumber}/${postgresConfig.databaseName}"
        )
        config.setUsername(postgresConfig.user)
        config.setPassword(postgresConfig.password)
        config.setMaximumPoolSize(postgresConfig.maxPoolSize)
        config
      }

      xa <- HikariTransactor.fromHikariConfig[Task](hikariConfig, defaultBlockingExecutor.asExecutionContext).toScopedZIO
    } yield xa
  }
}
