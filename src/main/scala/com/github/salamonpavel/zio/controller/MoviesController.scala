package com.github.salamonpavel.zio.controller

import com.github.salamonpavel.zio.model.Movie
import com.github.salamonpavel.zio.service.MoviesService
import com.github.salamonpavel.zio.util.{Constants, QueryParamsParser}
import zio.{ZIO, ZLayer}
import zio.http.QueryParams

trait MoviesController {
  def findById(queryParams: QueryParams): ZIO[Any, Throwable, Option[Movie]]
}

object MoviesController {
  def findById(queryParams: QueryParams): ZIO[MoviesController, Throwable, Option[Movie]] = {
    ZIO.serviceWithZIO[MoviesController](_.findById(queryParams))
  }
}

class MoviesControllerImpl(queryParamsParser: QueryParamsParser, moviesService: MoviesService) extends MoviesController {
  override def findById(queryParams: QueryParams): ZIO[Any, Throwable, Option[Movie]] = {
    for {
      id <- queryParamsParser.parseRequiredString(queryParams, Constants.id)
      movie <- moviesService.findMovieById(id.toInt) // toInt could fail on invalid input
    } yield movie
  }
}

object MoviesControllerImpl {
  val live: ZLayer[QueryParamsParser with MoviesService, Nothing, MoviesController] = ZLayer {
      for {
        queryParamsParser <- ZIO.service[QueryParamsParser]
        moviesService <- ZIO.service[MoviesService]
      } yield new MoviesControllerImpl(queryParamsParser, moviesService)
    }
}