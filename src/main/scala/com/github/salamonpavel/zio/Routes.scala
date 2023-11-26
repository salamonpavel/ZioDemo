package com.github.salamonpavel.zio

import com.github.salamonpavel.zio.controller.{ActorsController, MoviesController}
import com.github.salamonpavel.zio.exception.RequestBodyError
import com.github.salamonpavel.zio.model.{Actor, CreateActorRequestBody, Movie}
import zio._
import zio.http._
import zio.http.model.{Method, Status}
import zio.json.{DecoderOps, EncoderOps, JsonEncoder}

/**
 *  An object containing the HTTP routes for the application.
 */
object Routes {

  /**
   *  The routes for actor-related requests.
   */
  private val actorsRoutes: Http[ActorsController, Nothing, Request, Response] = Http.collectZIO[Request] {
    case req @ Method.GET -> !! / "actors" =>
      ActorsController
        .findById(req.url.queryParams)
        .map(responseFromOption[Actor])
        .catchAll(handleError)

    case req @ Method.POST -> !! / "actors" =>
      req.body.asString
        .flatMap(requestBody =>
          ZIO
            .fromEither(requestBody.fromJson[CreateActorRequestBody])
            .mapError(_ => RequestBodyError("Invalid request body"))
            .tapError(error => ZIO.logError(s"Failed to parse request body: ${error.message}"))
            .flatMap(createActorRequestBody =>
              ActorsController
                .create(createActorRequestBody)
                .map(_ => Response.status(Status.Created))
            )
        )
        .catchAll(_ => ZIO.succeed(Response.status(Status.InternalServerError)))
  }

  /**
   *  The routes for movie-related requests.
   */
  private val moviesRoutes: Http[MoviesController, Nothing, Request, Response] = Http.collectZIO[Request] {
    case req @ Method.GET -> !! / "movies" =>
      MoviesController
        .findById(req.url.queryParams)
        .map(responseFromOption[Movie])
        .catchAll(handleError)
  }

  /**
   *  All the routes for the application.
   */
  val allRoutes: Http[MoviesController with ActorsController, Nothing, Request, Response] =
    actorsRoutes ++ moviesRoutes

  /**
   *  Converts an option into a response.
   *  If the option is defined, its value is converted to JSON and returned in the response.
   *  If the option is empty, a 404 Not Found status is returned.
   *
   *  @param option The option to convert.
   *  @return The response.
   */
  private def responseFromOption[T: JsonEncoder](option: Option[T]): Response = option match {
    case Some(value) => Response.json(value.toJson)
    case None => Response.status(Status.NotFound)
  }

  /**
   *  Handles an error by returning a 400 Bad Request status with the error message.
   *
   *  @param error The error to handle.
   *  @return A ZIO effect that produces the response.
   */
  private def handleError(error: Throwable): ZIO[Any, Nothing, Response] = {
    ZIO.succeed(Response.text(s"Error: $error").setStatus(Status.BadRequest))
  }

}
