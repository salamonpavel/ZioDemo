package com.github.salamonpavel.zio.controller

import com.github.salamonpavel.zio.exception.AppError
import com.github.salamonpavel.zio.model.CreateActorRequestBody
import com.github.salamonpavel.zio.service.ActorsService
import com.github.salamonpavel.zio.util.{Constants, HttpRequestParser, HttpResponseBuilder}
import zio._
import zio.http._
import zio.http.model.Status

/**
 *  A trait representing the controller for actors.
 */
trait ActorsController {

  /**
   *  Finds an actor by ID.
   *
   *  @param request The HTTP request to find an actor.
   *  @return A ZIO effect that produces an Option of Actor. The effect may fail with an AppError.
   */
  def findActorById(request: Request): IO[AppError, Response]

  /**
   *  Creates an actor.
   *
   *  @param request The request to create an actor.
   *  @return A ZIO effect returning Unit as a result of Actor creation.
   *          The effect may fail with an AppError if the actor cannot be created.
   */
  def createActor(request: Request): IO[AppError, Response]
}

object ActorsController {

  /**
   *  Finds an actor by ID. This is an accessor method that requires an ActorsController.
   *
   *  @param request The HTTP request to find an actor.
   *  @return A ZIO effect that requires an ActorsController and produces an Option of Actor.
   *          The effect may fail with an AppError.
   */
  def findActorById(request: Request): ZIO[ActorsController, AppError, Response] = {
    ZIO.serviceWithZIO[ActorsController](_.findActorById(request))
  }

  /**
   *  Creates an actor. This is an accessor method that requires an ActorsController.
   *
   *  @param request The request to create an actor.
   *  @return A ZIO effect that requires an ActorsController and produces a Unit.
   *          The effect may fail with an AppError if the actor cannot be created.
   */
  def createActor(request: Request): ZIO[ActorsController, AppError, Response] = {
    ZIO.serviceWithZIO[ActorsController](_.createActor(request))
  }
}

/**
 *  An implementation of the ActorsController trait.
 */
class ActorsControllerImpl(
  httpRequestParser: HttpRequestParser,
  httpResponseBuilder: HttpResponseBuilder,
  actorsService: ActorsService
) extends ActorsController {

  /**
   *  Finds an actor by ID.
   *
   *  @param request The HTTP request to find an actor.
   *  @return A ZIO effect that produces an Option of Actor. The effect may fail with an AppError.
   */
  override def findActorById(request: Request): IO[AppError, Response] = {
    for {
      id       <- httpRequestParser.parseRequiredInt(request.url.queryParams, Constants.ID)
      actor    <- actorsService.findActorById(id)
      response <- ZIO.succeed(httpResponseBuilder.optionToResponse(actor))
    } yield response
  }

  /**
   *  Creates an actor.
   *
   *  @param request The request to create an actor.
   *  @return A ZIO effect returning Unit as a result of Actor creation.
   *          The effect may fail with an AppError if the actor cannot be created.
   */
  override def createActor(request: Request): IO[AppError, Response] = {
    for {
      createActorRequestBody <- httpRequestParser.parseRequestBody[CreateActorRequestBody](request)
      _                      <- actorsService.createActor(createActorRequestBody)
    } yield Response.status(Status.Created)
  }
}

object ActorsControllerImpl {

  /**
   *  A ZLayer that provides live implementation of ActorsController.
   */
  val live: URLayer[HttpRequestParser with HttpResponseBuilder with ActorsService, ActorsController] =
    ZLayer {
      for {
        httpRequestParser   <- ZIO.service[HttpRequestParser]
        httpResponseBuilder <- ZIO.service[HttpResponseBuilder]
        actorsService       <- ZIO.service[ActorsService]
      } yield new ActorsControllerImpl(httpRequestParser, httpResponseBuilder, actorsService)
    }
}
