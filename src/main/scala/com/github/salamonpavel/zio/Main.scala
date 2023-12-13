//package com.github.salamonpavel.zio
//
//import com.github.salamonpavel.zio.controller.{ActorsControllerImpl, MoviesControllerImpl}
//import com.github.salamonpavel.zio.database._
//import com.github.salamonpavel.zio.http.{HttpRequestParserImpl, HttpResponseBuilderImpl}
//import com.github.salamonpavel.zio.repository.{ActorsRepositoryImpl, MoviesRepositoryImpl}
//import com.github.salamonpavel.zio.service.{ActorsServiceImpl, MoviesServiceImpl}
//import zio.config.typesafe.TypesafeConfigProvider
//import zio.http.Server
//import zio.logging.consoleLogger
//import zio._
//
///**
// *  The main object of the application.
// */
//object Main extends ZIOAppDefault {
//
//  /**
//   *  The configuration provider of the application.
//   */
//  private val configProvider: ConfigProvider = TypesafeConfigProvider.fromResourcePath()
//
//  /**
//   *  The main method of the application.
//   *
//   *  @return A ZIO effect that represents the entire application.
//   */
//  override def run: ZIO[Any with ZIOAppArgs with Scope, Throwable, Any] =
//    Server
//      .serve(Routes.allRoutes)
//      .provide(
//        Server.default,
//        HttpRequestParserImpl.layer,
//        HttpResponseBuilderImpl.layer,
//        ActorsControllerImpl.layer,
//        ActorsServiceImpl.layer,
//        ActorsRepositoryImpl.layer,
//        MoviesControllerImpl.layer,
//        MoviesServiceImpl.layer,
//        MoviesRepositoryImpl.layer,
//        PostgresDatabaseProvider.layer,
//        GetActorById.layer,
//        GetActors.layer,
//        CreateActor.layer,
//        GetMovieById.layer,
////        ZLayer.Debug.mermaid
//      )
//
//  /**
//   *  The bootstrap layer of the application.
//   *  Logger settings are configured in the application.conf file.
//   */
//  override val bootstrap: ZLayer[Any, Config.Error, Unit] =
//    Runtime.removeDefaultLoggers >>> Runtime.setConfigProvider(configProvider) >>> consoleLogger()
//}
