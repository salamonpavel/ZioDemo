package com.github.salamonpavel.zio

import com.github.salamonpavel.zio.Constants._
import com.github.salamonpavel.zio.controller._
import com.github.salamonpavel.zio.model._
import sttp.tapir.PublicEndpoint
import sttp.tapir.generic.auto._
import sttp.tapir.json.play._
import sttp.tapir.ztapir._

trait ActorsRoutes extends BaseRoutes {

  // Tapir endpoints
  protected val getActorByIdEndpoint: PublicEndpoint[Int, ErrorApiResponse, SingleSuccessApiResponse[Actor], Any] =
    apiV1.get
      .name("getActorById")
      .in(Actors / path[Int](Id))
      .out(jsonBody[SingleSuccessApiResponse[Actor]])

  protected val getActorsEndpoint
    : PublicEndpoint[GetActorsQueryParameters, ErrorApiResponse, MultiSuccessApiResponse[Actor], Any] =
    apiV1.get
      .name("getActors")
      .in(Actors)
      .in(query[Option[String]](FirstName).and(query[Option[String]](LastName)).mapTo[GetActorsQueryParameters])
      .out(jsonBody[MultiSuccessApiResponse[Actor]])

  protected val createActorEndpoint
    : PublicEndpoint[CreateActorRequestBody, ErrorApiResponse, SingleSuccessApiResponse[Actor], Any] =
    apiV1.post
      .name("createActor")
      .in(Actors)
      .in(jsonBody[CreateActorRequestBody])
      .out(jsonBody[SingleSuccessApiResponse[Actor]])

  // Server endpoints
  protected val getActorByIdServerEndpoint: ZServerEndpoint[ActorsController, Any] =
    getActorByIdEndpoint.zServerLogic(ActorsController.findActorById)

  protected val getActorsServerEndpoint: ZServerEndpoint[ActorsController, Any] =
    getActorsEndpoint.zServerLogic(ActorsController.findActors)

  protected val createActorServerEndpoint: ZServerEndpoint[ActorsController, Any] =
    createActorEndpoint.zServerLogic(ActorsController.createActor)
}