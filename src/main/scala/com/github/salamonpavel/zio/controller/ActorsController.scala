package com.github.salamonpavel.zio.controller

import com.github.salamonpavel.zio.exception.AppError
import com.github.salamonpavel.zio.model.Actor
import com.github.salamonpavel.zio.service.ActorsService
import com.github.salamonpavel.zio.util.{Constants, QueryParamsParser}
import zio.http.QueryParams
import zio.{ZIO, ZLayer}

/**
 *  A trait representing the controller for actors.
 */
trait ActorsController {

  /**
   *  Finds an actor by ID.
   *
   *  @param queryParams The query parameters from the HTTP request.
   *  @return A ZIO effect that produces an Option of Actor. The effect may fail with an AppError.
   */
  def findById(queryParams: QueryParams): ZIO[Any, AppError, Option[Actor]]
}

object ActorsController {

  /**
   *  Finds an actor by ID. This is an accessor method that requires an ActorsController.
   *
   *  @param queryParams The query parameters from the HTTP request.
   *  @return A ZIO effect that requires an ActorsController and produces an Option of Actor.
   *          The effect may fail with an AppError.
   */
  def findById(queryParams: QueryParams): ZIO[ActorsController, AppError, Option[Actor]] = {
    ZIO.serviceWithZIO[ActorsController](_.findById(queryParams))
  }
}

/**
 *  An implementation of the ActorsController trait.
 */
class ActorsControllerImpl(queryParamsParser: QueryParamsParser, actorsService: ActorsService)
    extends ActorsController {

  /**
   *  Finds an actor by ID.
   *
   *  @param queryParams The query parameters from the HTTP request.
   *  @return A ZIO effect that produces an Option of Actor. The effect may fail with an AppError.
   */
  override def findById(queryParams: QueryParams): ZIO[Any, AppError, Option[Actor]] = {
    for {
      id <- queryParamsParser.parseRequiredInt(queryParams, Constants.ID)
      _ <- ZIO.logDebug("Trying to find an actor by ID.")
      actor <- actorsService.findActorById(id)
    } yield actor
  }
}

object ActorsControllerImpl {

  /**
   *  A ZLayer that provides live implementation of ActorsController.
   */
  val live: ZLayer[QueryParamsParser with ActorsService, Nothing, ActorsController] =
    ZLayer {
      for {
        queryParamsParser <- ZIO.service[QueryParamsParser]
        actorsService <- ZIO.service[ActorsService]
      } yield new ActorsControllerImpl(queryParamsParser, actorsService)
    }
}
