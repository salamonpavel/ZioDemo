package com.github.salamonpavel.zio.config

case class PostgresProperties(
  serverName: String,
  portNumber: Int,
  databaseName: String,
  user: String,
  password: String
)
