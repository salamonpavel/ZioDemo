package com.github.salamonpavel.zio.controller

import com.github.salamonpavel.zio.model.Actor
import com.github.salamonpavel.zio.service.ActorsService
import com.github.salamonpavel.zio.util.{Constants, QueryParamsParser}
import zio.{ZIO, ZLayer}
import zio.http.QueryParams

trait ActorsController {
  def findById(queryParams: QueryParams): ZIO[Any, Throwable, Option[Actor]]
}

object ActorsController {
  def findById(queryParams: QueryParams): ZIO[ActorsController, Throwable, Option[Actor]] = {
    ZIO.serviceWithZIO[ActorsController](_.findById(queryParams))
  }
}

class ActorsControllerImpl(queryParamsParser: QueryParamsParser, actorsService: ActorsService) extends ActorsController {
  override def findById(queryParams: QueryParams): ZIO[Any, Throwable, Option[Actor]] = {
    for {
      id <- queryParamsParser.parseRequiredString(queryParams, Constants.id)
      actor <- actorsService.findActorById(id.toInt) // toInt could fail on invalid input
    } yield actor
  }
}

object ActorsControllerImpl {
  val live: ZLayer[QueryParamsParser with ActorsService, Nothing, ActorsController] =
    ZLayer {
      for {
        queryParamsParser <- ZIO.service[QueryParamsParser]
        actorsService <- ZIO.service[ActorsService]
      } yield new ActorsControllerImpl(queryParamsParser, actorsService)
    }
}