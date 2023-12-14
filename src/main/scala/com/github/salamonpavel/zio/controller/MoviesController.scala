package com.github.salamonpavel.zio.controller

import com.github.salamonpavel.zio.model.{ApiResponseStatus, ErrorApiResponse, Movie, SingleSuccessApiResponse}
import com.github.salamonpavel.zio.service.MoviesService
import zio._
import zio.macros.accessible

/**
 *  A trait representing the controller for movies.
 */
@accessible
trait MoviesController {

  /**
   *  Finds a movie by ID.
   *
   *  @param id The ID of the movie to find.
   *  @return An IO[ErrorApiResponse, SingleSuccessApiResponse[Movie]] that will produce either an error response or 
   *          a single success response containing the movie.
   */
  def findMovieById(id: Int): IO[ErrorApiResponse, SingleSuccessApiResponse[Movie]]
}

/**
 *  An implementation of the MoviesController trait.
 */
class MoviesControllerImpl(moviesService: MoviesService) extends MoviesController {

  /**
   *  Finds a movie by ID.
   */
  override def findMovieById(id: Int): IO[ErrorApiResponse, SingleSuccessApiResponse[Movie]] = {
    moviesService
      .findMovieById(id)
      .flatMap {
        case Some(movie) => ZIO.succeed(SingleSuccessApiResponse(ApiResponseStatus.Success, movie))
        case None        => ZIO.fail(ErrorApiResponse(ApiResponseStatus.NotFound, s"Movie with id $id not found"))
      }
      .mapError {
        case apiResponse: ErrorApiResponse => apiResponse
        case error: Throwable              => ErrorApiResponse(ApiResponseStatus.InternalServerError, error.getMessage)
      }
  }
}

object MoviesControllerImpl {

  /**
   *  A ZLayer that provides live implementation of MoviesController.
   */
  val layer: URLayer[MoviesService, MoviesController] = ZLayer {
    for {
      moviesService <- ZIO.service[MoviesService]
    } yield new MoviesControllerImpl(moviesService)
  }
}
