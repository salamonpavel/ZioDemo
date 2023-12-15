package com.github.salamonpavel.zio

import com.github.salamonpavel.zio.Constants._
import com.github.salamonpavel.zio.model._
import sttp.tapir.{Endpoint, PublicEndpoint, endpoint}
import sttp.tapir.generic.auto._
import sttp.tapir.json.play._
import sttp.tapir.ztapir._

trait Endpoints {

  // Base endpoints
  private val baseEndpoint: Endpoint[Unit, Unit, ErrorApiResponse, Unit, Any] =
    endpoint.errorOut(jsonBody[ErrorApiResponse])

  private val apiV1: Endpoint[Unit, Unit, ErrorApiResponse, Unit, Any] =
    baseEndpoint.in(Api / V1)

  // Actors endpoints
  protected val getActorByIdEndpoint: PublicEndpoint[Int, ErrorApiResponse, SingleApiResponse[Actor], Any] =
    apiV1.get
      .name("getActorById")
      .in(Actors / path[Int](Id))
      .out(jsonBody[SingleApiResponse[Actor]])

  protected val getActorsEndpoint: PublicEndpoint[GetActorsParams, ErrorApiResponse, MultiApiResponse[Actor], Any] =
    apiV1.get
      .name("getActors")
      .in(Actors)
      .in(query[Option[String]](FirstName).and(query[Option[String]](LastName)).mapTo[GetActorsParams])
      .out(jsonBody[MultiApiResponse[Actor]])

  protected val createActorEndpoint
    : PublicEndpoint[CreateActorRequestBody, ErrorApiResponse, SingleApiResponse[Actor], Any] =
    apiV1.post
      .name("createActor")
      .in(Actors)
      .in(jsonBody[CreateActorRequestBody])
      .out(jsonBody[SingleApiResponse[Actor]])

  // Movies endpoints
  protected val getMovieByIdEndpoint: PublicEndpoint[Int, ErrorApiResponse, SingleApiResponse[Movie], Any] =
    apiV1.get
      .name("getMovieById")
      .in(Movies / path[Int](Id))
      .out(jsonBody[SingleApiResponse[Movie]])

}
