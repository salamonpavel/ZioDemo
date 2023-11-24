package com.github.salamonpavel.zio

import com.github.salamonpavel.zio.controller.ActorsControllerImpl
import com.github.salamonpavel.zio.service.{ActorsRepositoryImpl, ActorsServiceImpl}
import com.github.salamonpavel.zio.utils.QueryParamsParserImpl
import zio.http.Server
import zio.{Scope, ZIO, ZIOAppArgs, ZIOAppDefault}

object Main extends ZIOAppDefault {
  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    Server
      .serve(Routes.allRoutes)
      .provide(
        Server.default,
        ActorsControllerImpl.live,
        ActorsServiceImpl.live,
        ActorsRepositoryImpl.live,
        QueryParamsParserImpl.live
      )
}
