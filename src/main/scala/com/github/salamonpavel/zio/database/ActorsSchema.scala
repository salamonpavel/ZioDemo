package com.github.salamonpavel.zio.database

import com.github.salamonpavel.zio.database.ActorsSchemaImpl.{CreateActor, GetActorById}
import com.github.salamonpavel.zio.model.{Actor, CreateActorRequestBody}
import com.github.salamonpavel.zio.util.Constants.SCHEMA
import slick.jdbc.{GetResult, PositionedResult, SQLActionBuilder}
import za.co.absa.fadb.DBFunction._
import za.co.absa.fadb.DBSchema
import za.co.absa.fadb.naming.implementations.SnakeCaseNaming.Implicits._
import za.co.absa.fadb.slick.FaDbPostgresProfile.api._
import za.co.absa.fadb.slick.{SlickFunction, SlickFunctionWithStatusSupport, SlickPgEngine}
import za.co.absa.fadb.status.handling.implementations.StandardStatusHandling
import zio.{ZIO, ZLayer}

/**
 *  A trait representing the schema for actors.
 */
trait ActorsSchema {

  /**
   *  Gets an actor by ID.
   *
   *  @return An instance of GetActorById.
   */
  def getActorById: GetActorById

  /**
   *  Creates an actor.
   *
   *  @return An instance of CreateActor.
   */
  def createActor: CreateActor
}

/**
 *  An implementation of the ActorsSchema trait.
 */
class ActorsSchemaImpl(implicit dbEngine: SlickPgEngine) extends DBSchema(Some(SCHEMA)) with ActorsSchema {
  import ActorsSchemaImpl._

  val getActorById = new GetActorById()
  val createActor = new CreateActor()
}

object ActorsSchemaImpl {

  /**
   *  A class representing a function to get an actor by ID.
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

  /**
   *  A ZLayer that provides live implementation of ActorsSchema.
   */
  val live: ZLayer[PostgresDatabaseProvider, Nothing, ActorsSchema] = ZLayer {
    for {
      dbProvider <- ZIO.service[PostgresDatabaseProvider]
    } yield new ActorsSchemaImpl()(dbProvider.dbEngine)
  }

}
