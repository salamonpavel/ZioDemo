package com.github.salamonpavel.zio.database

import com.github.salamonpavel.zio.database.MoviesSchemaImpl.GetMovieById
import com.github.salamonpavel.zio.model.Movie
import com.github.salamonpavel.zio.util.Constants.schema
import slick.jdbc.{GetResult, PositionedResult, SQLActionBuilder}
import za.co.absa.fadb.DBFunction._
import za.co.absa.fadb.DBSchema
import za.co.absa.fadb.naming.implementations.SnakeCaseNaming.Implicits._
import za.co.absa.fadb.slick.FaDbPostgresProfile.api._
import za.co.absa.fadb.slick.{SlickFunction, SlickPgEngine}
import zio.{ZIO, ZLayer}

/**
 * A trait representing the schema for movies.
 */
trait MoviesSchema {
  /**
   * Gets a movie by ID.
   *
   * @return An instance of GetMovieById.
   */
  def getMovieById: GetMovieById
}

/**
 * An implementation of the MoviesSchema trait.
 */
class MoviesSchemaImpl(implicit dbEngine: SlickPgEngine) extends DBSchema(Some(schema)) with MoviesSchema {
  import MoviesSchemaImpl._

  val getMovieById = new GetMovieById()
}

object MoviesSchemaImpl {

  /**
   * A class representing a function to get a movie by ID.
   */
  class GetMovieById(implicit override val schema: DBSchema, val dbEngine: SlickPgEngine)
    extends DBOptionalResultFunction[Int, Movie, SlickPgEngine] with SlickFunction[Int, Movie] {

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

  /**
   * A ZLayer that provides live implementation of MoviesSchema.
   */
  val live: ZLayer[PostgresDatabaseProvider, Nothing, MoviesSchema] = ZLayer {
      for {
        dbProvider <- ZIO.service[PostgresDatabaseProvider]
      } yield new MoviesSchemaImpl()(dbProvider.dbEngine)
    }

}
