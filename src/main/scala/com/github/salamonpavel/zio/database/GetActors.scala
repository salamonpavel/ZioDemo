package com.github.salamonpavel.zio.database

import com.github.salamonpavel.zio.model.{Actor, GetActorsParams}
import slick.jdbc.SQLActionBuilder
import za.co.absa.fadb.DBSchema
import za.co.absa.fadb.slick.FaDbPostgresProfile.api._
import za.co.absa.fadb.slick.SlickFunction.SlickMultipleResultFunction
import za.co.absa.fadb.slick.SlickPgEngine
import zio.{ZIO, ZLayer}

/**
 *  A class representing a function to get a movie by ID.
 */
class GetActors(implicit override val schema: DBSchema, val dbEngine: SlickPgEngine)
    extends SlickMultipleResultFunction[GetActorsParams, Actor]
    with ActorSlickConverter {

  override def fieldsToSelect: Seq[String] = super.fieldsToSelect ++ Seq("actor_id", "first_name", "last_name")

  override protected def sql(values: GetActorsParams): SQLActionBuilder = {
    sql"""SELECT #$selectEntry FROM #$functionName(${values.firstName},${values.lastName}) #$alias;"""
  }
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
