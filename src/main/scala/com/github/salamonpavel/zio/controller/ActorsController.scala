package com.github.salamonpavel.zio.controller

import com.github.salamonpavel.zio.Constants.{FirstName, LastName}
import com.github.salamonpavel.zio.http.{HttpRequestParser, HttpResponseBuilder}
import com.github.salamonpavel.zio.model._
import com.github.salamonpavel.zio.service.ActorsService
import zio._
import zio.http._

/**
 *  A trait representing the controller for actors.
 */
trait ActorsController {

  /**
   *  Finds an actor by ID.
   *
   *  @param id The ID of the actor to find.
   *  @return A UIO[Response] that, when run, will produce an HTTP response.
   *         The response will contain the actor if it was found, or an error message if an error occurred.
   */
  def findActorById(id: Int): UIO[Response]

  /**
   *  Finds actors by first name and/or last name.
   *
   *  @param request The request to find actors.
   *  @return A UIO[Response] that, when run, will produce an HTTP response.
   *         The response will contain the actors if they were found, or an error message if an error occurred.
   */
  def findActors(request: Request): UIO[Response]

  /**
   *  Creates an actor.
   *
   *  @param request The request to create an actor.
   *  @return A UIO[Response] that, when run, will produce an HTTP response.
   *         The response will contain the created actor if it was created, or an error message if an error occurred.
   */
  def createActor(request: Request): UIO[Response]
}

object ActorsController {

  /**
   *  Finds an actor by ID. This is an accessor method that requires an ActorsController.
   */
  def findActorById(id: Int): URIO[ActorsController, Response] = {
    ZIO.serviceWithZIO[ActorsController](_.findActorById(id))
  }

  /**
   *  Finds actors by first name and/or last name. This is an accessor method that requires an ActorsController.
   */
  def findActors(request: Request): URIO[ActorsController, Response] = {
    ZIO.serviceWithZIO[ActorsController](_.findActors(request))
  }

  /**
   *  Creates an actor. This is an accessor method that requires an ActorsController.
   */
  def createActor(request: Request): URIO[ActorsController, Response] = {
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
  override def findActorById(id: Int): UIO[Response] = {
    actorsService
      .findActorById(id)
      .fold(
        error => httpResponseBuilder.appErrorToResponse(error),
        actor => httpResponseBuilder.optionToResponse(actor)
      )
  }

  /**
   *  Finds actors by first name and/or last name.
   */
  override def findActors(request: Request): UIO[Response] = {
    (for {
      firstName <- httpRequestParser.getOptionalStringParam(request.url.queryParams, FirstName)
      lastName  <- httpRequestParser.getOptionalStringParam(request.url.queryParams, LastName)
      actors    <- actorsService.findActors(GetActorsQueryParameters(firstName, lastName))
    } yield actors).fold(
      error => httpResponseBuilder.appErrorToResponse(error),
      actors => httpResponseBuilder.seqToResponse(actors)
    )
  }

  /**
   *  Creates an actor.
   */
  override def createActor(request: Request): UIO[Response] = {
    httpRequestParser
      .parseRequestBody[CreateActorRequestBody](request)
      .flatMap(requestBody => actorsService.createActor(requestBody))
      .fold(
        error => httpResponseBuilder.appErrorToResponse(error),
        actor => httpResponseBuilder.successPostResponse(actor)
      )
  }
}

object ActorsControllerImpl {

  /**
   *  A ZLayer that provides live implementation of ActorsController.
   */
  val layer: URLayer[HttpRequestParser with HttpResponseBuilder with ActorsService, ActorsController] =
    ZLayer {
      for {
        httpRequestParser   <- ZIO.service[HttpRequestParser]
        httpResponseBuilder <- ZIO.service[HttpResponseBuilder]
        actorsService       <- ZIO.service[ActorsService]
      } yield new ActorsControllerImpl(httpRequestParser, httpResponseBuilder, actorsService)
    }
}
