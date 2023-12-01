package com.github.salamonpavel.zio.controller

import com.github.salamonpavel.zio.http.{HttpRequestParser, HttpResponseBuilder}
import com.github.salamonpavel.zio.service.MoviesService
import zio._
import zio.http.Response

/**
 *  A trait representing the controller for movies.
 */
trait MoviesController {

  /**
   *  Finds a movie by ID.
   *
   *  @param id The ID of the movie to find.
   *  @return A UIO[Response] that, when run, will produce an HTTP response.
   *         The response will contain the movie if it was found, or an error message if an error occurred.
   */
  def findMovieById(id: Int): UIO[Response]
}

object MoviesController {

  /**
   *  Finds a movie by ID. This is an accessor method that requires a MoviesController.
   */
  def findMovieById(id: Int): URIO[MoviesController, Response] = {
    ZIO.serviceWithZIO[MoviesController](_.findMovieById(id))
  }
}

/**
 *  An implementation of the MoviesController trait.
 */
class MoviesControllerImpl(
  httpResponseBuilder: HttpResponseBuilder,
  moviesService: MoviesService
) extends MoviesController {

  /**
   *  Finds a movie by ID.
   */
  override def findMovieById(id: Int): UIO[Response] = {
    moviesService
      .findMovieById(id)
      .fold(
        error => httpResponseBuilder.appErrorToResponse(error),
        movie => httpResponseBuilder.optionToResponse(movie)
      )
  }
}

object MoviesControllerImpl {

  /**
   *  A ZLayer that provides live implementation of MoviesController.
   */
  val live: URLayer[HttpRequestParser with HttpResponseBuilder with MoviesService, MoviesController] = ZLayer {
    for {
      httpResponseBuilder <- ZIO.service[HttpResponseBuilder]
      moviesService       <- ZIO.service[MoviesService]
    } yield new MoviesControllerImpl(httpResponseBuilder, moviesService)
  }
}
