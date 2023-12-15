package com.github.salamonpavel.zio.repository

import com.github.salamonpavel.zio.database.GetMovieById
import com.github.salamonpavel.zio.exception.DatabaseError
import com.github.salamonpavel.zio.model.Movie
import zio._
import zio.macros.accessible

import scala.concurrent.ExecutionContext

/**
 *  A trait representing the repository for movies.
 */
@accessible
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
class MoviesRepositoryImpl(getMovieByIdFn: GetMovieById) extends MoviesRepository {

  /**
   *  Gets a movie by ID.
   */
  override def getMovieById(id: Int): IO[DatabaseError, Option[Movie]] = {
    ZIO
      .fromFuture { implicit ec: ExecutionContext => getMovieByIdFn(id) }
      .tap {
        case Some(movie) => ZIO.logInfo(s"Retrieved movie with ID $id: $movie")
        case None        => ZIO.logInfo(s"Movie with ID $id not found.")
      }
      .mapError(error => DatabaseError(error.getMessage))
      .tapError(error => ZIO.logError(s"Failed to retrieve movie with ID $id: ${error.message}"))
  }
}

object MoviesRepositoryImpl {

  /**
   *  A ZLayer that provides live implementation of MoviesRepository.
   */
  val layer: URLayer[GetMovieById, MoviesRepository] = ZLayer {
    for {
      getMovieByIdFn <- ZIO.service[GetMovieById]
    } yield new MoviesRepositoryImpl(getMovieByIdFn)
  }
}
