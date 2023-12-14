package com.github.salamonpavel.zio

import com.github.salamonpavel.zio.Constants.{Api, V1}
import com.github.salamonpavel.zio.model.ErrorApiResponse
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.play._

trait BaseRoutes {

  protected val baseEndpoint: Endpoint[Unit, Unit, ErrorApiResponse, Unit, Any] =
    endpoint.errorOut(jsonBody[ErrorApiResponse])

  protected val apiV1: Endpoint[Unit, Unit, ErrorApiResponse, Unit, Any] =
    baseEndpoint.in(Api / V1)
}