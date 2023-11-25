package com.github.salamonpavel.zio.controller

import com.github.salamonpavel.zio.exception.AppError
import com.github.salamonpavel.zio.model.Movie
import com.github.salamonpavel.zio.service.MoviesService
import com.github.salamonpavel.zio.util.{Constants, QueryParamsParser}
import zio.{ZIO, ZLayer}
import zio.http.QueryParams

/**
 *  A trait representing the controller for movies.
 */
trait MoviesController {

  /**
   *  Finds a movie by ID.
   *
   *  @param queryParams The query parameters from the HTTP request.
   *  @return A ZIO effect that produces an Option of Movie. The effect may fail with an AppError.
   */
  def findById(queryParams: QueryParams): ZIO[Any, AppError, Option[Movie]]
}

object MoviesController {

  /**
   *  Finds a movie by ID. This is an accessor method that requires a MoviesController.
   *
   *  @param queryParams The query parameters from the HTTP request.
   *  @return A ZIO effect that requires a MoviesController and produces an Option of Movie.
   *          The effect may fail with an AppError.
   */
  def findById(queryParams: QueryParams): ZIO[MoviesController, AppError, Option[Movie]] = {
    ZIO.serviceWithZIO[MoviesController](_.findById(queryParams))
  }
}

/**
 *  An implementation of the MoviesController trait.
 */
class MoviesControllerImpl(queryParamsParser: QueryParamsParser, moviesService: MoviesService)
    extends MoviesController {

  /**
   *  Finds a movie by ID.
   *
   *  @param queryParams The query parameters from the HTTP request.
   *  @return A ZIO effect that produces an Option of Movie. The effect may fail with an AppError.
   */
  override def findById(queryParams: QueryParams): ZIO[Any, AppError, Option[Movie]] = {
    for {
      id <- queryParamsParser.parseRequiredInt(queryParams, Constants.ID)
      movie <- moviesService.findMovieById(id)
    } yield movie
  }
}

object MoviesControllerImpl {

  /**
   *  A ZLayer that provides live implementation of MoviesController.
   */
  val live: ZLayer[QueryParamsParser with MoviesService, Nothing, MoviesController] = ZLayer {
    for {
      queryParamsParser <- ZIO.service[QueryParamsParser]
      moviesService <- ZIO.service[MoviesService]
    } yield new MoviesControllerImpl(queryParamsParser, moviesService)
  }
}
