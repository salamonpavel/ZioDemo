package com.github.salamonpavel.zio

import com.github.salamonpavel.zio.Constants._
import com.github.salamonpavel.zio.controller.{ActorsController, MoviesController}
import zio.http._
import zio.http.model.Method

/**
 *  An object containing the HTTP routes for the application.
 */
object Routes {

  /**
   *  The routes for actor-related requests.
   */
  private val actorsRoutes = Http.collectZIO[Request] {
    case Method.GET -> !! / API / V1 / ACTORS / int(id) =>
      ActorsController.findActorById(id)

    case request @ Method.GET -> !! / API / V1 / ACTORS => // todo: implement pagination
      ActorsController.findActors(request)

    case request @ Method.POST -> !! / API / V1 / ACTORS =>
      ActorsController.createActor(request)
  }

  /**
   *  The routes for movie-related requests.
   */
  private val moviesRoutes = Http.collectZIO[Request] {
    case Method.GET -> !! / API / V1 / MOVIES / int(id) =>
      MoviesController.findMovieById(id)
  }

  val allRoutes: Http[MoviesController with ActorsController, Nothing, Request, Response] =
    actorsRoutes ++ moviesRoutes
}
