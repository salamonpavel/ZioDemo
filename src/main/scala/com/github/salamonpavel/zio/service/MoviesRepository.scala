package com.github.salamonpavel.zio.service

import com.github.salamonpavel.zio.database.MoviesSchema
import com.github.salamonpavel.zio.model.Movie
import zio.{ZIO, ZLayer}

trait MoviesRepository {
  def getMovieById(id: Int): ZIO[Any, Throwable, Option[Movie]]
}

object MoviesRepository {
  def getMovieById(id: Int): ZIO[MoviesRepository, Throwable, Option[Movie]] = {
    ZIO.serviceWithZIO[MoviesRepository](_.getMovieById(id))
  }
}

class MoviesRepositoryImpl(schema: MoviesSchema) extends MoviesRepository {
  override def getMovieById(id: Int): ZIO[Any, Throwable, Option[Movie]] = {
    ZIO.fromFuture { implicit ec => schema.getMovieById(id) }
  }
}

object MoviesRepositoryImpl {
  val live: ZLayer[MoviesSchema, Nothing, MoviesRepository] = ZLayer {
    for {
      schema <- ZIO.service[MoviesSchema]
    } yield new MoviesRepositoryImpl(schema)
  }
}