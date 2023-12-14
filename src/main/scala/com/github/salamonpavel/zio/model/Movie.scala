package com.github.salamonpavel.zio.model

import play.api.libs.json.{Json, Reads, Writes}

/**
 *  A case class representing a movie.
 *
 *  @param movieId The ID of the movie.
 *  @param movieName The name of the movie.
 *  @param movieLength The length of the movie.
 */
case class Movie(movieId: Int, movieName: String, movieLength: Int)

object Movie {

  /**
   *  A JSON encoder/decoder for the Movie class.
   */
  implicit val reads: Reads[Movie] = Json.reads[Movie]
  implicit val writes: Writes[Movie] = Json.writes[Movie]
}
