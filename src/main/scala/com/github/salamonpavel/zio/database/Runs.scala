package com.github.salamonpavel.zio.database

import com.github.salamonpavel.zio.model.Actor
import slick.jdbc.{GetResult, PositionedResult, SQLActionBuilder}
import za.co.absa.fadb.DBFunction._
import za.co.absa.fadb.DBSchema
import za.co.absa.fadb.naming.implementations.SnakeCaseNaming.Implicits._
import za.co.absa.fadb.slick.FaDbPostgresProfile.api._
import za.co.absa.fadb.slick.{SlickFunctionWithStatusSupport, SlickPgEngine}
import za.co.absa.fadb.status.handling.implementations.StandardStatusHandling

class Runs {
  import Runs._
}

object Runs {

  class GetActorById //(implicit override val schema: DBSchema, override val dbEngine: SlickPgEngine)
    extends DBOptionalResultFunction[Integer, Actor, SlickPgEngine]
      with SlickFunctionWithStatusSupport[Integer, Actor]
      with StandardStatusHandling {

    override protected def sql(values: Integer): SQLActionBuilder = {
      sql"""SELECT #$selectEntry FROM #$functionName($values) #$alias;"""
    }

    override protected def slickConverter: GetResult[Actor] = {
      def converter(r: PositionedResult): Actor = Actor(r.<<, r.<<, r.<<, r.<<, r.<<)
      GetResult(converter)
    }

  }

}
