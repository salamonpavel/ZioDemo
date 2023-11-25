package com.github.salamonpavel.zio

import com.github.salamonpavel.zio.controller.{ActorsController, MoviesController}
import com.github.salamonpavel.zio.model.{Actor, Movie}
import zio._
import zio.http._
import zio.json.{EncoderOps, JsonEncoder}
import zio.http.codec.PathCodec.int
import zio.http.model.{Method, Status}


object Routes {

  val actorsRoutes: Http[ActorsController, Nothing, Request, Response] = Http.collectZIO[Request] {
    case req@Method.GET -> !! / "actors" =>
      ActorsController
        .findById(req.url.queryParams)
        .map(responseFromOption[Actor])
        .catchAll(handleError)
  }
  
  val moviesRoutes: Http[MoviesController, Nothing, Request, Response] = Http.collectZIO[Request] {
      case req@Method.GET -> !! / "movies" =>
        MoviesController
          .findById(req.url.queryParams)
          .map(responseFromOption[Movie])
          .catchAll(handleError)
    }

  val allRoutes: Http[MoviesController with ActorsController, Nothing, Request, Response] = actorsRoutes ++ moviesRoutes

  private def responseFromOption[T: JsonEncoder](option: Option[T]): Response = option match {
    case Some(value) => Response.json(value.toJson)
    case None => Response.status(Status.NotFound)
  }

  private def handleError(error: Throwable): ZIO[Any, Nothing, Response] = {
    ZIO.succeed(Response.text(s"Error: $error").setStatus(Status.BadRequest))
  }
    
}
