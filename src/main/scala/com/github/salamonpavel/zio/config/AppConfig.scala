package com.github.salamonpavel.zio.config

import com.typesafe.config.ConfigFactory
import zio.{ULayer, ZLayer}

/**
 *  A class representing a provider of application configuration.
 */
object AppConfig { // not needed at the moment, config <- ZIO.service[config.Config]

  /**
   *  A ZLayer that provides live implementation of application configuration.
   */
  val layer: ULayer[com.typesafe.config.Config] = ZLayer.succeed(ConfigFactory.load())
}
