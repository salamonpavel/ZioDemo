package com.github.salamonpavel.zio.database

import com.github.salamonpavel.zio.model.CreateActorRequestBody
import slick.jdbc.{GetResult, SQLActionBuilder}
import za.co.absa.fadb.DBSchema
import za.co.absa.fadb.slick.FaDbPostgresProfile.api._
import za.co.absa.fadb.slick.SlickFunction.SlickSingleResultFunction
import za.co.absa.fadb.slick.SlickPgEngine
import za.co.absa.fadb.status.handling.implementations.StandardStatusHandling
import zio._

/**
 *  A class representing a function to create an actor.
 */
class CreateActor(implicit override val schema: DBSchema, val dbEngine: SlickPgEngine)
    extends SlickSingleResultFunction[CreateActorRequestBody, Int] {

  override def fieldsToSelect: Seq[String] = super.fieldsToSelect ++ Seq("o_actor_id")

  override protected def sql(createActorRequestBody: CreateActorRequestBody): SQLActionBuilder = {
    sql"""SELECT #$selectEntry
            FROM #$functionName(
              ${createActorRequestBody.firstName},
              ${createActorRequestBody.lastName}
            ) #$alias;"""
  }

  protected def slickConverter: GetResult[Int] = GetResult(r => r.<<)
}

object CreateActor {

  /**
   *  A ZLayer that provides live implementation of CreateActor.
   */
  val layer: ZLayer[PostgresDatabaseProvider, Nothing, CreateActor] = ZLayer {
    for {
      dbProvider <- ZIO.service[PostgresDatabaseProvider]
    } yield new CreateActor()(Runs, dbProvider.dbEngine)
  }
}
