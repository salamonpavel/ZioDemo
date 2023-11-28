package com.github.salamonpavel.zio.service

import com.github.salamonpavel.zio.exception.DatabaseError
import com.github.salamonpavel.zio.model.Movie
import com.github.salamonpavel.zio.repository.MoviesRepository
import zio._

/**
 *  A trait representing the repository for movies.
 */
trait MoviesService {

  /**
   *  Gets a movie by ID. This is an accessor method that requires a MoviesService.
   *
   *  @param id The ID of the movie.
   *  @return A ZIO effect that requires a MoviesService and produces an Option of Movie.
   *          The effect may fail with a DatabaseError.
   */
  def findMovieById(id: Int): IO[DatabaseError, Option[Movie]]
}

/**
 *  An implementation of the MoviesService trait.
 *
 *  @param moviesRepository The MoviesRepository that this service will use to interact with the database.
 */
class MoviesServiceImpl(moviesRepository: MoviesRepository) extends MoviesService {

  /**
   *  Gets a movie by ID.
   */
  override def findMovieById(id: Int): IO[DatabaseError, Option[Movie]] = {
    moviesRepository.getMovieById(id)
  }
}

object MoviesServiceImpl {

  /**
   *  A ZLayer that provides live implementation of MoviesService.
   */
  val live: URLayer[MoviesRepository, MoviesService] = ZLayer {
    for {
      moviesRepository <- ZIO.service[MoviesRepository]
    } yield new MoviesServiceImpl(moviesRepository)
  }
}
