package com.github.salamonpavel.zio

import com.github.salamonpavel.zio.controller.{ActorsControllerImpl, MoviesControllerImpl}
import com.github.salamonpavel.zio.database.{ActorsSchemaImpl, MoviesSchemaImpl, PostgresDatabaseProvider}
import com.github.salamonpavel.zio.repository.{ActorsRepositoryImpl, MoviesRepositoryImpl}
import com.github.salamonpavel.zio.service.{ActorsServiceImpl, MoviesServiceImpl}
import com.github.salamonpavel.zio.util.QueryParamsParserImpl
import zio.http.Server
import zio.{Scope, ZIO, ZIOAppArgs, ZIOAppDefault}

/**
 *  The main object of the application.
 */
object Main extends ZIOAppDefault {

  /**
   *  The main method of the application.
   *
   *  @return A ZIO effect that represents the entire application.
   */
  override def run: ZIO[Any with ZIOAppArgs with Scope, Throwable, Any] =
    Server
      .serve(Routes.allRoutes)
      .provide(
        Server.default,
        QueryParamsParserImpl.live,
        ActorsControllerImpl.live,
        ActorsServiceImpl.live,
        ActorsRepositoryImpl.live,
        ActorsSchemaImpl.live,
        MoviesControllerImpl.live,
        MoviesServiceImpl.live,
        MoviesRepositoryImpl.live,
        MoviesSchemaImpl.live,
        PostgresDatabaseProvider.live
      )
}
