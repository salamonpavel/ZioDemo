package com.github.salamonpavel.zio.controller

import com.github.salamonpavel.zio.model._
import com.github.salamonpavel.zio.service.ActorsService
import zio._
import zio.macros.accessible

/**
 *  A trait representing the controller for actors.
 */
@accessible
trait ActorsController {

  /**
   *  Finds an actor by ID.
   *
   *  @param id The ID of the actor to find.
   *  @return An IO[ErrorApiResponse, SingleApiResponse[Actor]] that will produce either an error response or
   *          a single success response containing the actor.
   */
  def findActorById(id: Int): IO[ErrorApiResponse, SingleApiResponse[Actor]]

  /**
   *  Finds actors by first name and/or last name.
   *
   *  @param params The query parameters to use when finding actors.
   *  @return An IO[ErrorApiResponse, MultiApiResponse[Actor]] that will produce either an error response or
   *          a multi success response containing the actors.
   */
  def findActors(params: GetActorsParams): IO[ErrorApiResponse, MultiApiResponse[Actor]]

  /**
   *  Creates an actor.
   *
   *  @param requestBody The request body to create an actor.
   *  @return An IO[ErrorApiResponse, SingleApiResponse[Actor]] that will produce either an error response or
   *          a single success response containing the created actor.
   */
  def createActor(requestBody: CreateActorRequestBody): IO[ErrorApiResponse, SingleApiResponse[Actor]]
}

/**
 *  An implementation of the ActorsController trait.
 */
class ActorsControllerImpl(actorsService: ActorsService) extends ActorsController {

  /**
   *  Finds an actor by ID.
   */
  override def findActorById(id: Int): ZIO[Any, ErrorApiResponse, SingleApiResponse[Actor]] = {
    actorsService
      .findActorById(id)
      .flatMap {
        case Some(actor) => ZIO.succeed(SingleApiResponse(ApiResponseStatus.Success, actor))
        case None        => ZIO.fail(ErrorApiResponse(ApiResponseStatus.NotFound, s"Actor with id $id not found"))
      }
      .mapError {
        case apiResponse: ErrorApiResponse => apiResponse
        case error: Throwable              => ErrorApiResponse(ApiResponseStatus.InternalServerError, error.getMessage)
      }
  }

  /**
   *  Finds actors by first name and/or last name.
   */
  override def findActors(params: GetActorsParams): IO[ErrorApiResponse, MultiApiResponse[Actor]] = {
    actorsService
      .findActors(params)
      .foldZIO(
        error => ZIO.fail(ErrorApiResponse(ApiResponseStatus.InternalServerError, error.message)),
        actors => ZIO.succeed(MultiApiResponse(ApiResponseStatus.Success, actors))
      )
  }

  /**
   *  Creates an actor.
   */
  override def createActor(
    requestBody: CreateActorRequestBody
  ): ZIO[Any, ErrorApiResponse, SingleApiResponse[Actor]] = {
    actorsService
      .createActor(requestBody)
      .foldZIO(
        error => ZIO.fail(ErrorApiResponse(ApiResponseStatus.InternalServerError, error.message)),
        actor => ZIO.succeed(SingleApiResponse(ApiResponseStatus.Created, actor))
      )
  }

}

object ActorsControllerImpl {

  /**
   *  A ZLayer that provides live implementation of ActorsController.
   */
  val layer: URLayer[ActorsService, ActorsController] =
    ZLayer {
      for {
        actorsService <- ZIO.service[ActorsService]
      } yield new ActorsControllerImpl(actorsService)
    }
}
