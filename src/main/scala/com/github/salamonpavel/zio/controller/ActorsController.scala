package com.github.salamonpavel.zio.controller

import com.github.salamonpavel.zio.Constants.{FIRST_NAME, LAST_NAME}
import com.github.salamonpavel.zio.exception.AppError
import com.github.salamonpavel.zio.http.{HttpRequestParser, HttpResponseBuilder}
import com.github.salamonpavel.zio.model._
import com.github.salamonpavel.zio.service.ActorsService
import play.api.libs.json.Json
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
   *  @param id The ID of the actor.
   *  @return A ZIO effect that produces an Option of Actor. The effect may fail with an AppError.
   */
  def findActorById(id: Int): IO[AppError, Response]

  def findActors(request: Request): IO[AppError, Response]

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
   */
  def findActorById(id: Int): ZIO[ActorsController, AppError, Response] = {
    ZIO.serviceWithZIO[ActorsController](_.findActorById(id))
  }

  def findActors(request: Request): ZIO[ActorsController, AppError, Response] = {
    ZIO.serviceWithZIO[ActorsController](_.findActors(request))
  }

  /**
   *  Creates an actor. This is an accessor method that requires an ActorsController.
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
   */
  override def findActorById(id: Int): IO[AppError, Response] = {
    for {
      actor    <- actorsService.findActorById(id)
      response <- ZIO.succeed(httpResponseBuilder.optionToResponse(actor))
    } yield response
  }

  override def findActors(request: Request): IO[AppError, Response] = {
    for {
      firstName <- httpRequestParser.getOptionalStringParam(request.url.queryParams, FIRST_NAME)
      lastName  <- httpRequestParser.getOptionalStringParam(request.url.queryParams, LAST_NAME)
      actors    <- actorsService.findActors(GetActorsQueryParameters(firstName, lastName))
      response <- ZIO.succeed {
        Response.json(Json.toJson(MultiApiResponse[Actor](ApiResponseStatus.Success, actors)).toString)
      }
    } yield response
  }

  /**
   *  Creates an actor.
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
