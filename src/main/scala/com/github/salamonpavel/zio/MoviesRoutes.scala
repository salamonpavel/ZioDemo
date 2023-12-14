package com.github.salamonpavel.zio

import com.github.salamonpavel.zio.Constants._
import com.github.salamonpavel.zio.controller._
import com.github.salamonpavel.zio.model._
import sttp.tapir.PublicEndpoint
import sttp.tapir.generic.auto._
import sttp.tapir.json.play._
import sttp.tapir.ztapir._

trait MoviesRoutes extends BaseRoutes {

  // Movie endpoint
  protected val getMovieByIdEndpoint: PublicEndpoint[Int, ErrorApiResponse, SingleSuccessApiResponse[Movie], Any] =
    apiV1.get
      .name("getMovieById")
      .in(Movies / path[Int](Id))
      .out(jsonBody[SingleSuccessApiResponse[Movie]])

  // Server endpoint
  protected val getMovieByIdServerEndpoint: ZServerEndpoint[MoviesController, Any] =
    getMovieByIdEndpoint.zServerLogic(MoviesController.findMovieById)
}
