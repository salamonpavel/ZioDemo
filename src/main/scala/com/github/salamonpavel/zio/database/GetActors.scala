package com.github.salamonpavel.zio.database

import com.github.salamonpavel.zio.model.{Actor, GetActorsParams}
import doobie.implicits.toSqlInterpolator
import doobie.util.Read
import doobie.util.fragment.Fragment
import za.co.absa.fadb.DBSchema
import za.co.absa.fadb.doobiedb.DoobieEngine
import za.co.absa.fadb.doobiedb.DoobieFunction.DoobieMultipleResultFunction
import zio._
import zio.interop.catz._

/**
 *  A class representing a function to get a movie by ID.
 */
class GetActors(implicit override val schema: DBSchema, override val dbEngine: DoobieEngine[Task])
    extends DoobieMultipleResultFunction[GetActorsParams, Actor, Task] {

  override def sql(values: GetActorsParams)(implicit read: Read[Actor]): Fragment =
    sql"SELECT actor_id, first_name, last_name FROM ${Fragment.const(functionName)}(${values.firstName}, ${values.lastName})"
}

object GetActors {

  /**
   *  A ZLayer that provides live implementation of GetMovieById.
   */
  val layer: ZLayer[PostgresDatabaseProvider, Nothing, GetActors] = ZLayer {
    for {
      dbProvider <- ZIO.service[PostgresDatabaseProvider]
    } yield new GetActors()(Runs, dbProvider.dbEngine)
  }
}
