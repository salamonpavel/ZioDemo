package com.github.salamonpavel.zio

import com.github.salamonpavel.zio.Constants.{ACTORS, MOVIES}
import com.github.salamonpavel.zio.controller.{ActorsController, MoviesController}
import com.github.salamonpavel.zio.exception.AppError
import zio._
import zio.http._
import zio.http.model.{Method, Status}

/**
 *  An object containing the HTTP routes for the application.
 */
object Routes {

  /**
   *  The routes for actor-related requests.
   */
  private val actorsRoutes = Http.collectZIO[Request] {
    case request @ Method.GET -> !! / ACTORS =>
      ActorsController.findActorById(request).catchAll(handleError)

    case request @ Method.POST -> !! / ACTORS =>
      ActorsController.createActor(request).catchAll(handleError)
  }

  /**
   *  The routes for movie-related requests.
   */
  private val moviesRoutes = Http.collectZIO[Request] { case request @ Method.GET -> !! / MOVIES =>
    MoviesController.findMovieById(request).catchAll(handleError)
  }

  val allRoutes: Http[MoviesController with ActorsController, Nothing, Request, Response] =
    actorsRoutes ++ moviesRoutes

  /**
   *  Handles an error by returning an appropriate response.
   *
   *  @param error The error to handle.
   *  @return A ZIO effect that produces the response.
   */
  private def handleError(error: Throwable): UIO[Response] = {
    error match {
      case appError: AppError =>
        ZIO.succeed(
          Response
            .text(s"An error occurred while processing your request: ${appError.message}")
            .setStatus(Status.BadRequest)
        )
      case _ =>
        ZIO.succeed(
          Response
            .text("An unexpected error occurred. Please try again later.")
            .setStatus(Status.InternalServerError)
        )
    }
  }

}
