package com.github.salamonpavel.zio.model

import zio.json.{DeriveJsonEncoder, JsonEncoder}

/**
 * A case class representing a movie.
 *
 * @param movieId The ID of the movie.
 * @param movieName The name of the movie.
 * @param movieLength The length of the movie.
 */
case class Movie(movieId: Int, movieName: String, movieLength: Int)

object Movie {
  /**
   * An implicit JsonEncoder for the Movie case class.
   * This encoder is used to convert instances of Movie to JSON.
   */
  implicit val encoder: JsonEncoder[Movie] = DeriveJsonEncoder.gen[Movie]
}
