package com.github.salamonpavel.zio.database

import com.github.salamonpavel.zio.model.CreateActorRequestBody
import doobie.implicits.toSqlInterpolator
import doobie.util.Read
import doobie.util.fragment.Fragment
import za.co.absa.fadb.DBSchema
import za.co.absa.fadb.doobiedb.DoobieEngine
import za.co.absa.fadb.doobiedb.DoobieFunction.DoobieSingleResultFunction
import zio._
import zio.interop.catz._

/**
 *  A class representing a function to create an actor.
 */
class CreateActor(implicit override val schema: DBSchema, override val dbEngine: DoobieEngine[Task])
    extends DoobieSingleResultFunction[CreateActorRequestBody, Int, Task] {

  override def sql(values: CreateActorRequestBody)(implicit read: Read[Int]): Fragment = {
    sql"SELECT o_actor_id FROM ${Fragment.const(functionName)}(${values.firstName}, ${values.lastName})"
  }
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
