package com.github.salamonpavel.zio.service

import com.github.salamonpavel.zio.model.Movie
import zio.{ZIO, ZLayer}

trait MoviesService {
  def findMovieById(id: Int): ZIO[Any, Throwable, Option[Movie]]
}

object MoviesService {
  def findMovieById(id: Int): ZIO[MoviesService, Throwable, Option[Movie]] = {
    ZIO.serviceWithZIO[MoviesService](_.findMovieById(id))
  }
}

class MoviesServiceImpl(moviesRepository: MoviesRepository) extends MoviesService {
  override def findMovieById(id: Int): ZIO[Any, Throwable, Option[Movie]] = {
    for {
      movie <- moviesRepository.getMovieById(id)
    } yield movie
  }
}


object MoviesServiceImpl {
  val live: ZLayer[MoviesRepository, Nothing, MoviesService] = ZLayer {
      for {
        moviesRepository <- ZIO.service[MoviesRepository]
      } yield new MoviesServiceImpl(moviesRepository)
    }
}