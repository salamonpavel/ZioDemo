package com.github.salamonpavel.zio.database

import doobie.Transactor
import za.co.absa.fadb.doobiedb.DoobieEngine
import zio._
import zio.interop.catz._

/**
 *  A class representing a provider of Postgres database.
 */
class PostgresDatabaseProvider(val dbEngine: DoobieEngine[Task])

object PostgresDatabaseProvider {

  /**
   *  A ZLayer that provides live implementation of PostgresDatabaseProvider.
   */
  val layer: RLayer[Transactor[Task], PostgresDatabaseProvider] = ZLayer {
    for {
      transactor <- ZIO.service[Transactor[Task]]
      doobieEngine <- ZIO.succeed(new DoobieEngine[Task](transactor))
    } yield new PostgresDatabaseProvider(doobieEngine)
  }
}
