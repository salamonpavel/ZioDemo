package com.github.salamonpavel.zio

import com.github.salamonpavel.zio.Constants._
import com.github.salamonpavel.zio.model._
import sttp.model.StatusCode
import sttp.tapir.generic.auto._
import sttp.tapir.json.play._
import sttp.tapir.ztapir._
import sttp.tapir.{Endpoint, PublicEndpoint, endpoint}

trait Endpoints {

  // Base endpoints
  private val baseEndpoint: PublicEndpoint[Unit, ErrorApiResponse, Unit, Any] =
    endpoint.errorOut(
      oneOf[ErrorApiResponse](
        oneOfVariantValueMatcher(
          StatusCode.BadRequest,
          jsonBody[ErrorApiResponse]
            .description("bad request")
            .example(ErrorApiResponse(ApiResponseStatus.BadRequest, "Decoding error for an input value 'value'."))
        ) { case ErrorApiResponse(ApiResponseStatus.BadRequest, _) =>
          true
        },
        oneOfVariantValueMatcher(
          StatusCode.InternalServerError,
          jsonBody[ErrorApiResponse]
            .description("internal server error")
            .example(ErrorApiResponse(ApiResponseStatus.InternalServerError, "Error description."))
        ) { case ErrorApiResponse(ApiResponseStatus.InternalServerError, _) =>
          true
        }
      )
    )

  private val apiV1: Endpoint[Unit, Unit, ErrorApiResponse, Unit, Any] =
    baseEndpoint.in(Api / V1)

  private val notFoundOneOfVariant =
    oneOfVariantValueMatcher(
      StatusCode.NotFound,
      jsonBody[ErrorApiResponse]
        .description("not found")
        .example(ErrorApiResponse(ApiResponseStatus.NotFound, "A record with id 'id' not found."))
    ) { case ErrorApiResponse(ApiResponseStatus.NotFound, _) =>
      true
    }

  // Actors endpoints
  protected val getActorByIdEndpoint: PublicEndpoint[Int, ErrorApiResponse, SingleApiResponse[Actor], Any] =
    apiV1.get
      .name("getActorById")
      .in(Actors / path[Int](Id))
      .out(jsonBody[SingleApiResponse[Actor]])
      .errorOutVariantPrepend(notFoundOneOfVariant)

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
      .out(statusCode(StatusCode.Created))
      .out(jsonBody[SingleApiResponse[Actor]])

  // Movies endpoints
  protected val getMovieByIdEndpoint: PublicEndpoint[Int, ErrorApiResponse, SingleApiResponse[Movie], Any] =
    apiV1.get
      .name("getMovieById")
      .in(Movies / path[Int](Id))
      .out(jsonBody[SingleApiResponse[Movie]])
      .errorOutVariantPrepend(notFoundOneOfVariant)

}
