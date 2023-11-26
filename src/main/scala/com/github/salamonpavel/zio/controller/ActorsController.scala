package com.github.salamonpavel.zio.controller

import com.github.salamonpavel.zio.exception.AppError
import com.github.salamonpavel.zio.model.{Actor, CreateActorRequestBody}
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

  /**
   *  Creates an actor.
   *
   *  @param createActorRequestBody The request to create an actor.
   *  @return A ZIO effect returning Unit as a result of Actor creation.
   *          The effect may fail with an AppError if the actor cannot be created.
   */
  def create(createActorRequestBody: CreateActorRequestBody): ZIO[Any, AppError, Unit]
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

  /**
   *  Creates an actor. This is an accessor method that requires an ActorsController.
   *
   *  @param createActorRequestBody The request to create an actor.
   *  @return A ZIO effect that requires an ActorsController and produces a Unit.
   *          The effect may fail with an AppError if the actor cannot be created.
   */
  def create(createActorRequestBody: CreateActorRequestBody): ZIO[ActorsController, AppError, Unit] = {
    ZIO.serviceWithZIO[ActorsController](_.create(createActorRequestBody))
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

  /**
   *  Creates an actor.
   *
   *  @param createActorRequestBody The request to create an actor.
   *  @return A ZIO effect returning Unit as a result of Actor creation.
   *          The effect may fail with an AppError if the actor cannot be created.
   */
  override def create(createActorRequestBody: CreateActorRequestBody): ZIO[Any, AppError, Unit] = {
    for {
      _ <- ZIO.logDebug(
        s"Trying to create an actor with first name ${createActorRequestBody.firstName} and last name ${createActorRequestBody.lastName}."
      )
      _ <- actorsService.createActor(createActorRequestBody)
    } yield ()
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
