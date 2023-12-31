package com.github.salamonpavel.zio

import com.github.salamonpavel.zio.Constants.{Api, V1}
import com.github.salamonpavel.zio.model.{ApiResponseStatus, ErrorApiResponse}
import sttp.model.StatusCode
import sttp.tapir.generic.auto.schemaForCaseClass
import sttp.tapir.{Endpoint, EndpointOutput, PublicEndpoint}
import sttp.tapir.json.play.jsonBody
import sttp.tapir.ztapir._

trait BaseEndpoints {

  private val badRequestOneOfVariant: EndpointOutput.OneOfVariant[ErrorApiResponse] = {
    oneOfVariantValueMatcher(
      StatusCode.BadRequest, jsonBody[ErrorApiResponse].description("bad request")
        .example(ErrorApiResponse(ApiResponseStatus.BadRequest, "Decoding error for an input value 'value'."))
    ) {
      case ErrorApiResponse(ApiResponseStatus.BadRequest, _) => true
    }
  }

  private val internalServerErrorOneOfVariant: EndpointOutput.OneOfVariant[ErrorApiResponse] = {
    oneOfVariantValueMatcher(
      StatusCode.InternalServerError, jsonBody[ErrorApiResponse].description("internal server error")
        .example(ErrorApiResponse(ApiResponseStatus.InternalServerError, "Error description."))
    ) {
      case ErrorApiResponse(ApiResponseStatus.InternalServerError, _) => true
    }
  }

  protected val notFoundOneOfVariant: EndpointOutput.OneOfVariant[ErrorApiResponse] = {
    oneOfVariantValueMatcher(
      StatusCode.NotFound, jsonBody[ErrorApiResponse].description("not found")
        .example(ErrorApiResponse(ApiResponseStatus.NotFound, "A record with id 'id' not found."))
    ) {
      case ErrorApiResponse(ApiResponseStatus.NotFound, _) => true
    }
  }

  // Base endpoints
  private val baseEndpoint: PublicEndpoint[Unit, ErrorApiResponse, Unit, Any] = {
    endpoint.errorOut(
      oneOf[ErrorApiResponse](
        badRequestOneOfVariant, internalServerErrorOneOfVariant
      )
    )
  }

  protected val apiV1: Endpoint[Unit, Unit, ErrorApiResponse, Unit, Any] = {
    baseEndpoint.in(Api / V1)
  }

}
