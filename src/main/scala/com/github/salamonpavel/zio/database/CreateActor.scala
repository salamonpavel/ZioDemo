package com.github.salamonpavel.zio.database

import com.github.salamonpavel.zio.model.CreateActorRequestBody
import slick.jdbc.{GetResult, SQLActionBuilder}
import za.co.absa.fadb.DBFunction._
import za.co.absa.fadb.DBSchema
import za.co.absa.fadb.slick.FaDbPostgresProfile.api._
import za.co.absa.fadb.slick.{SlickFunctionWithStatusSupport, SlickPgEngine}
import za.co.absa.fadb.status.handling.implementations.StandardStatusHandling
import zio._

/**
 *  A class representing a function to create an actor.
 */
class CreateActor(implicit override val schema: DBSchema, val dbEngine: SlickPgEngine)
    extends DBSingleResultFunction[CreateActorRequestBody, Unit, SlickPgEngine]
    with SlickFunctionWithStatusSupport[CreateActorRequestBody, Unit]
    with StandardStatusHandling {

  override protected def sql(createActorRequestBody: CreateActorRequestBody): SQLActionBuilder = {
    sql"""SELECT #$selectEntry
            FROM #$functionName(
              ${createActorRequestBody.firstName},
              ${createActorRequestBody.lastName}
            ) #$alias;"""
  }

  override protected def slickConverter: GetResult[Unit] = GetResult { _ => }
}

object CreateActor {

  /**
   *  A ZLayer that provides live implementation of CreateActor.
   */
  val live: ZLayer[PostgresDatabaseProvider, Nothing, CreateActor] = ZLayer {
    for {
      dbProvider <- ZIO.service[PostgresDatabaseProvider]
    } yield new CreateActor()(Runs, dbProvider.dbEngine)
  }
}
