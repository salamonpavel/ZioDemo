package com.github.salamonpavel.zio.database

import com.github.salamonpavel.zio.model.Actor
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
class GetActorById(implicit schema: DBSchema, dbEngine: DoobieEngine[Task])
  extends DoobieOptionalResultFunction[Int, Actor, Task] {

  override def sql(values: Int)(implicit read: Read[Actor]): Fragment =
    sql"SELECT actor_id, first_name, last_name FROM ${Fragment.const(functionName)}($values)"
}

object GetActorById {

  /**
   *  A ZLayer that provides live implementation of GetMovieById.
   */
  val layer: ZLayer[PostgresDatabaseProvider, Nothing, GetActorById] = ZLayer {
    for {
      dbProvider <- ZIO.service[PostgresDatabaseProvider]
    } yield new GetActorById()(Runs, dbProvider.dbEngine)
  }
}
