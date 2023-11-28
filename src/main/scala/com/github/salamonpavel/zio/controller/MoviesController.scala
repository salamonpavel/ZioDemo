package com.github.salamonpavel.zio.controller

import com.github.salamonpavel.zio.exception.AppError
import com.github.salamonpavel.zio.service.MoviesService
import com.github.salamonpavel.zio.util.{Constants, HttpRequestParser, HttpResponseBuilder}
import zio._
import zio.http.{Request, Response}

/**
 *  A trait representing the controller for movies.
 */
trait MoviesController {

  /**
   *  Finds a movie by ID.
   *
   *  @param request The HTTP request to find a movie.
   *  @return A ZIO effect that produces an Option of Movie. The effect may fail with an AppError.
   */
  def findMovieById(request: Request): IO[AppError, Response]
}

object MoviesController {

  /**
   *  Finds a movie by ID. This is an accessor method that requires a MoviesController.
   */
  def findMovieById(request: Request): ZIO[MoviesController, AppError, Response] = {
    ZIO.serviceWithZIO[MoviesController](_.findMovieById(request))
  }
}

/**
 *  An implementation of the MoviesController trait.
 */
class MoviesControllerImpl(
  queryParamsParser: HttpRequestParser,
  httpResponseBuilder: HttpResponseBuilder,
  moviesService: MoviesService
) extends MoviesController {

  /**
   *  Finds a movie by ID.
   */
  override def findMovieById(request: Request): IO[AppError, Response] = {
    for {
      id       <- queryParamsParser.parseRequiredInt(request.url.queryParams, Constants.ID)
      movie    <- moviesService.findMovieById(id)
      response <- ZIO.succeed(httpResponseBuilder.optionToResponse(movie))
    } yield response
  }
}

object MoviesControllerImpl {

  /**
   *  A ZLayer that provides live implementation of MoviesController.
   */
  val live: URLayer[HttpRequestParser with HttpResponseBuilder with MoviesService, MoviesController] = ZLayer {
    for {
      httpRequestParser   <- ZIO.service[HttpRequestParser]
      httpResponseBuilder <- ZIO.service[HttpResponseBuilder]
      moviesService       <- ZIO.service[MoviesService]
    } yield new MoviesControllerImpl(httpRequestParser, httpResponseBuilder, moviesService)
  }
}
