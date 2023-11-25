package com.github.salamonpavel.zio.service

import com.github.salamonpavel.zio.database.MoviesSchema
import com.github.salamonpavel.zio.exception.DatabaseError
import com.github.salamonpavel.zio.model.Movie
import zio.{ZIO, ZLayer}

import scala.concurrent.ExecutionContext

/**
 *  A trait representing the repository for movies.
 */
trait MoviesRepository {

  /**
   *  Gets a movie by ID. This is an accessor method that requires a MoviesRepository.
   *
   *  @param id The ID of the movie.
   *  @return A ZIO effect that requires a MoviesRepository and produces an Option of Movie.
   *          The effect may fail with a DatabaseError.
   */
  def getMovieById(id: Int): ZIO[Any, DatabaseError, Option[Movie]]
}

object MoviesRepository {

  /**
   *  Gets a movie by ID. This is an accessor method that requires a MoviesRepository.
   *
   *  @param id The ID of the movie.
   *  @return A ZIO effect that requires a MoviesRepository and produces an Option of Movie.
   *          The effect may fail with a DatabaseError.
   */
  def getMovieById(id: Int): ZIO[MoviesRepository, DatabaseError, Option[Movie]] = {
    ZIO.serviceWithZIO[MoviesRepository](_.getMovieById(id))
  }
}

/**
 *  An implementation of the MoviesRepository trait.
 */
class MoviesRepositoryImpl(schema: MoviesSchema) extends MoviesRepository {

  /**
   *  Gets a movie by ID.
   *
   *  @param id The ID of the movie.
   *  @return A ZIO effect that produces an Option of Movie. The effect may fail with a DatabaseError.
   */
  override def getMovieById(id: Int): ZIO[Any, DatabaseError, Option[Movie]] = {
    ZIO
      .fromFuture { implicit ec: ExecutionContext => schema.getMovieById(id) }
      .mapError(error => DatabaseError(error.getMessage))
  }
}

object MoviesRepositoryImpl {

  /**
   *  A ZLayer that provides live implementation of MoviesRepository.
   */
  val live: ZLayer[MoviesSchema, Nothing, MoviesRepository] = ZLayer {
    for {
      schema <- ZIO.service[MoviesSchema]
    } yield new MoviesRepositoryImpl(schema)
  }
}
