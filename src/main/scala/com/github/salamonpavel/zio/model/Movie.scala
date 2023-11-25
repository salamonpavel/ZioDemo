package com.github.salamonpavel.zio.model

import zio.json.{DeriveJsonEncoder, JsonEncoder}

case class Movie(movieId: Int, movieName: String, movieLength: Int)

object Movie {
  implicit val encoder: JsonEncoder[Movie] = DeriveJsonEncoder.gen[Movie]
}
