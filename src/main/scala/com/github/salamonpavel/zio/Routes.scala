package com.github.salamonpavel.zio

import com.github.salamonpavel.zio.controller.ActorsController
import zio.ZIO
import zio.http.{Http, Request, Response}
import zio.http.middleware.RequestHandlerMiddlewares
import zio.http.model.{Method, Status}
import zio.json.EncoderOps

object Routes {
  val allRoutes: Http[ActorsController, Nothing, Request, Response] =
    Http.collectZIO[Request] {
      case req@Method.GET -> !! =>
        ActorsController
          .findById(req.url.queryParams)
          .map {
            case Some(value) => Response.json(value.toJson)
            case None => Response.status(Status.NotFound)
          }
          .catchAll(error => ZIO.succeed(Response.text(s"caught an error: $error").setStatus(Status.BadRequest)))
    } @@ RequestHandlerMiddlewares.debug
}
