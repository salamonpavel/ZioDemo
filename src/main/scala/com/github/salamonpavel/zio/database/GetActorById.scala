package com.github.salamonpavel.zio.database

import com.github.salamonpavel.zio.model.Actor
import slick.jdbc.{GetResult, PositionedResult, SQLActionBuilder}
import za.co.absa.fadb.DBFunction._
import za.co.absa.fadb.DBSchema
import za.co.absa.fadb.slick.FaDbPostgresProfile.api._
import za.co.absa.fadb.slick.{SlickFunction, SlickPgEngine}
import zio.{ZIO, ZLayer}

/**
 *  A class representing a function to get a movie by ID.
 */
class GetActorById(implicit override val schema: DBSchema, val dbEngine: SlickPgEngine)
    extends DBOptionalResultFunction[Int, Actor, SlickPgEngine]
    with SlickFunction[Int, Actor] {

  override def fieldsToSelect: Seq[String] = super.fieldsToSelect ++ Seq("actor_id", "first_name", "last_name")

  override protected def sql(values: Int): SQLActionBuilder = {
    sql"""SELECT #$selectEntry FROM #$functionName($values) #$alias;"""
  }

  override protected def slickConverter: GetResult[Actor] = {
    def converter(r: PositionedResult): Actor = {
      Actor(r.<<, r.<<, r.<<)
    }
    GetResult(converter)
  }
}

object GetActorById {

  /**
   *  A ZLayer that provides live implementation of GetMovieById.
   */
  val live: ZLayer[PostgresDatabaseProvider, Nothing, GetActorById] = ZLayer {
    for {
      dbProvider <- ZIO.service[PostgresDatabaseProvider]
    } yield new GetActorById()(Runs, dbProvider.dbEngine)
  }
}
