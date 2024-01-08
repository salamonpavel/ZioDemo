package com.github.salamonpavel.zio.database

import com.github.salamonpavel.zio.config.PostgresConfig
import com.zaxxer.hikari.HikariConfig
import doobie.HC
import doobie.hikari.HikariTransactor
import doobie.util.transactor.{Strategy, Transactor}
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
      xa <- HikariTransactor
        .fromHikariConfig[Task](hikariConfig, defaultBlockingExecutor.asExecutionContext)
        .toScopedZIO
    } yield xa
  }

  val testLayer: ZLayer[Any, Config.Error, Transactor[Task]] = ZLayer {
    for {
      postgresConfig <- ZIO.config[PostgresConfig](PostgresConfig.config)
      transactor <- ZIO.succeed(
        Transactor.fromDriverManager[Task](
          postgresConfig.dataSourceClass,
          s"jdbc:postgresql://${postgresConfig.serverName}:${postgresConfig.portNumber}/${postgresConfig.databaseName}",
          postgresConfig.user,
          postgresConfig.password,
        )
      )
      transactorWithRollback = Transactor.strategy.set(transactor, Strategy.default.copy(after = HC.rollback))
    } yield transactorWithRollback
  }
}
