package com.github.salamonpavel.zio

import com.github.salamonpavel.zio.controller.ActorsControllerImpl
import com.github.salamonpavel.zio.database.{ActorsSchemaImpl, PostgresDatabaseProvider}
import com.github.salamonpavel.zio.service.{ActorsRepositoryImpl, ActorsServiceImpl}
import com.github.salamonpavel.zio.util.QueryParamsParserImpl
import zio.http.Server
import zio.{Scope, ZIO, ZIOAppArgs, ZIOAppDefault}

object Main extends ZIOAppDefault {
  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    Server
      .serve(Routes.allRoutes)
      .provide(
        Server.default,
        QueryParamsParserImpl.live,
        ActorsControllerImpl.live,
        ActorsServiceImpl.live,
        ActorsRepositoryImpl.live,
        ActorsSchemaImpl.live,
        PostgresDatabaseProvider.live
      )
}
