package com.github.salamonpavel.zio.config

import zio.Config
import zio.config.magnolia.deriveConfig

case class PostgresConfig(
  connectionPool: String,
  dataSourceClass: String,
  properties: PostgresProperties,
  numThreads: Int
)

object PostgresConfig {
  val config: Config[PostgresConfig] = deriveConfig[PostgresConfig].nested("postgres")
}
