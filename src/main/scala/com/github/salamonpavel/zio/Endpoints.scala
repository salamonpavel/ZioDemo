package com.github.salamonpavel.zio

import com.github.salamonpavel.zio.Constants._
import com.github.salamonpavel.zio.model._
import sttp.tapir.{Endpoint, PublicEndpoint, endpoint}
import sttp.tapir.generic.auto._
import sttp.tapir.json.play._
import sttp.tapir.ztapir._

trait Endpoints {

  // Base endpoints
  protected val baseEndpoint: Endpoint[Unit, Unit, ErrorApiResponse, Unit, Any] =
    endpoint.errorOut(jsonBody[ErrorApiResponse])

  protected val apiV1: Endpoint[Unit, Unit, ErrorApiResponse, Unit, Any] =
    baseEndpoint.in(Api / V1)
    
  // Actors endpoints
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

  // Movies endpoints
  protected val getMovieByIdEndpoint: PublicEndpoint[Int, ErrorApiResponse, SingleSuccessApiResponse[Movie], Any] =
    apiV1.get
      .name("getMovieById")
      .in(Movies / path[Int](Id))
      .out(jsonBody[SingleSuccessApiResponse[Movie]])


}