package com.github.salamonpavel.zio

import com.github.salamonpavel.zio.controller.{ActorsControllerImpl, MoviesControllerImpl}
import com.github.salamonpavel.zio.database._
import com.github.salamonpavel.zio.repository.{ActorsRepositoryImpl, MoviesRepositoryImpl}
import com.github.salamonpavel.zio.service.{ActorsServiceImpl, MoviesServiceImpl}
import zio._
import zio.config.typesafe.TypesafeConfigProvider
import zio.logging.consoleLogger

/**
 *  The main object of the application.
 */
object Main extends ZIOAppDefault with Server {

  /**
   *  The configuration provider of the application.
   */
  private val configProvider: ConfigProvider = TypesafeConfigProvider.fromResourcePath()

  /**
   *  The main method of the application.
   *
   *  @return A ZIO effect that represents the entire application.
   */
  override def run: ZIO[Any, Throwable, Unit] =
    server
      .provide(
        ActorsControllerImpl.layer,
        MoviesControllerImpl.layer,
        ActorsServiceImpl.layer,
        MoviesServiceImpl.layer,
        ActorsRepositoryImpl.layer,
        MoviesRepositoryImpl.layer,
        PostgresDatabaseProvider.layer,
        GetActorById.layer,
        GetActors.layer,
        CreateActor.layer,
        GetMovieById.layer,
        ZLayer.Debug.mermaid
      )

  /**
   *  The bootstrap layer of the application.
   *  Logger settings are configured in the application.conf file.
   */
  override val bootstrap: ZLayer[Any, Config.Error, Unit] =
    Runtime.removeDefaultLoggers >>> Runtime.setConfigProvider(configProvider) >>> consoleLogger()
}
