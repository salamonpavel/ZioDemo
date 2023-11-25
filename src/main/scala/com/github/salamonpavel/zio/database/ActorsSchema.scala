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

trait ActorsSchema {
  def getActorById: GetActorById
}

class ActorsSchemaImpl(implicit dbEngine: SlickPgEngine) extends DBSchema(Some(schema)) with ActorsSchema {
  import ActorsSchemaImpl._

  val getActorById = new GetActorById()
}

object ActorsSchemaImpl {

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

  val live: ZLayer[PostgresDatabaseProvider, Nothing, ActorsSchema] = ZLayer {
      for {
        dbProvider <- ZIO.service[PostgresDatabaseProvider]
      } yield new ActorsSchemaImpl()(dbProvider.dbEngine)
    }

}
