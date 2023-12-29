package com.github.salamonpavel.zio.controller

import com.github.salamonpavel.zio.exception.ServiceError
import com.github.salamonpavel.zio.model.{ApiResponseStatus, ErrorApiResponse, Movie, SingleApiResponse}
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
  def findMovieById(id: Int): IO[ErrorApiResponse, SingleApiResponse[Movie]]
}

/**
 *  An implementation of the MoviesController trait.
 */
class MoviesControllerImpl(moviesService: MoviesService) extends MoviesController {

  /**
   *  Finds a movie by ID.
   */
  override def findMovieById(id: Int): IO[ErrorApiResponse, SingleApiResponse[Movie]] = {
    moviesService
      .findMovieById(id)
      .mapError { serviceError: ServiceError =>
        ErrorApiResponse(ApiResponseStatus.InternalServerError, serviceError.getMessage)
      }
      .flatMap {
        case Some(movie) => ZIO.succeed(SingleApiResponse(ApiResponseStatus.Success, movie))
        case None        => ZIO.fail(ErrorApiResponse(ApiResponseStatus.NotFound, s"Movie with id $id not found"))
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
