package com.github.salamonpavel.zio.database

import com.github.salamonpavel.zio.model.Movie
import doobie.implicits.toSqlInterpolator
import doobie.util.Read
import doobie.util.fragment.Fragment
import za.co.absa.fadb.DBSchema
import za.co.absa.fadb.doobiedb.DoobieEngine
import za.co.absa.fadb.doobiedb.DoobieFunction.DoobieOptionalResultFunction
import zio._
import zio.interop.catz._

/**
 *  A class representing a function to get a movie by ID.
 */
class GetMovieById(implicit override val schema: DBSchema, override val dbEngine: DoobieEngine[Task])
    extends DoobieOptionalResultFunction[Int, Movie, Task] {

  override def sql(values: Int)(implicit read: Read[Movie]): Fragment = {
    sql"SELECT movie_id, movie_name, movie_length FROM ${Fragment.const(functionName)}($values)"
  }
}

object GetMovieById {

  /**
   *  A ZLayer that provides live implementation of GetMovieById.
   */
  val layer: ZLayer[PostgresDatabaseProvider, Nothing, GetMovieById] = ZLayer {
    for {
      dbProvider <- ZIO.service[PostgresDatabaseProvider]
    } yield new GetMovieById()(Runs, dbProvider.dbEngine)
  }
}
