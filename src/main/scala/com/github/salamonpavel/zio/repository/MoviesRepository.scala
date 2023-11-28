package com.github.salamonpavel.zio.repository

import com.github.salamonpavel.zio.database.MoviesSchema
import com.github.salamonpavel.zio.exception.DatabaseError
import com.github.salamonpavel.zio.model.Movie
import zio._

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
  def getMovieById(id: Int): IO[DatabaseError, Option[Movie]]
}

/**
 *  An implementation of the MoviesRepository trait.
 */
class MoviesRepositoryImpl(schema: MoviesSchema) extends MoviesRepository {

  /**
   *  Gets a movie by ID.
   */
  override def getMovieById(id: Int): IO[DatabaseError, Option[Movie]] = {
    ZIO
      .fromFuture { implicit ec: ExecutionContext => schema.getMovieById(id) }
      .tap {
        case Some(movie) => ZIO.logInfo(s"Retrieved movie with ID $id: $movie")
        case None => ZIO.logInfo(s"Movie with ID $id not found.")
      }
      .mapError(error => DatabaseError(error.getMessage))
      .tapError(error => ZIO.logError(s"Failed to retrieve movie with ID $id: ${error.message}"))
  }
}

object MoviesRepositoryImpl {

  /**
   *  A ZLayer that provides live implementation of MoviesRepository.
   */
  val live: URLayer[MoviesSchema, MoviesRepository] = ZLayer {
    for {
      schema <- ZIO.service[MoviesSchema]
    } yield new MoviesRepositoryImpl(schema)
  }
}
