package com.github.salamonpavel.zio.database

import com.github.salamonpavel.zio.model.Movie
import slick.jdbc.{GetResult, PositionedResult, SQLActionBuilder}
import za.co.absa.fadb.DBFunction._
import za.co.absa.fadb.DBSchema
import za.co.absa.fadb.slick.FaDbPostgresProfile.api._
import za.co.absa.fadb.slick.{SlickFunction, SlickPgEngine}
import zio._

/**
 *  A class representing a function to get a movie by ID.
 */
class GetMovieById(implicit override val schema: DBSchema, val dbEngine: SlickPgEngine)
    extends DBOptionalResultFunction[Int, Movie, SlickPgEngine]
    with SlickFunction[Int, Movie] {

  override def fieldsToSelect: Seq[String] = super.fieldsToSelect ++ Seq("movie_id", "movie_name", "movie_length")

  override protected def sql(values: Int): SQLActionBuilder = {
    sql"""SELECT #$selectEntry FROM #$functionName($values) #$alias;"""
  }

  override protected def slickConverter: GetResult[Movie] = {
    def converter(r: PositionedResult): Movie = {
      Movie(r.<<, r.<<, r.<<)
    }

    GetResult(converter)
  }
}

object GetMovieById {

  /**
   *  A ZLayer that provides live implementation of GetMovieById.
   */
  val live: ZLayer[PostgresDatabaseProvider, Nothing, GetMovieById] = ZLayer {
    for {
      dbProvider <- ZIO.service[PostgresDatabaseProvider]
    } yield new GetMovieById()(Runs, dbProvider.dbEngine)
  }
}
