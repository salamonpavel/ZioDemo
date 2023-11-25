package com.github.salamonpavel.zio.database

import com.github.salamonpavel.zio.database.ActorsSchemaImpl.GetActorById
import com.github.salamonpavel.zio.model.Actor
import com.github.salamonpavel.zio.util.Constants.schema
import slick.jdbc.{GetResult, PositionedResult, SQLActionBuilder}
import za.co.absa.fadb.DBFunction._
import za.co.absa.fadb.DBSchema
import za.co.absa.fadb.naming.implementations.SnakeCaseNaming.Implicits._
import za.co.absa.fadb.slick.FaDbPostgresProfile.api._
import za.co.absa.fadb.slick.{SlickFunction, SlickPgEngine}
import zio.{ZIO, ZLayer}

/** 
 * A trait representing the schema for actors.
 */
trait ActorsSchema {
  /**
   * Gets an actor by ID.
   *
   * @return An instance of GetActorById.
   */
  def getActorById: GetActorById
}

/** 
 * An implementation of the ActorsSchema trait.
 */
class ActorsSchemaImpl(implicit dbEngine: SlickPgEngine) extends DBSchema(Some(schema)) with ActorsSchema {
  import ActorsSchemaImpl._

  val getActorById = new GetActorById()
}

object ActorsSchemaImpl {

  /**
   * A class representing a function to get an actor by ID.
   */
  class GetActorById(implicit override val schema: DBSchema,  val dbEngine: SlickPgEngine)
    extends DBOptionalResultFunction[Int, Actor, SlickPgEngine] with SlickFunction[Int, Actor] {

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

  /**
   * A ZLayer that provides live implementation of ActorsSchema.
   */
  val live: ZLayer[PostgresDatabaseProvider, Nothing, ActorsSchema] = ZLayer {
      for {
        dbProvider <- ZIO.service[PostgresDatabaseProvider]
      } yield new ActorsSchemaImpl()(dbProvider.dbEngine)
    }

}
