package com.github.salamonpavel.zio.service

import com.github.salamonpavel.zio.database.ActorsSchema
import com.github.salamonpavel.zio.model.Actor
import zio.{ZIO, ZLayer}

import scala.concurrent.ExecutionContext

trait ActorsRepository {
  def getActorById(id: Int): ZIO[Any, Throwable, Option[Actor]]
}

object ActorsRepository {
  def getActorById(id: Int): ZIO[ActorsRepository, Throwable, Option[Actor]] = {
    ZIO.serviceWithZIO[ActorsRepository](_.getActorById(id))
  }
}

class ActorsRepositoryImpl(schema: ActorsSchema) extends ActorsRepository {
  override def getActorById(id: Int): ZIO[Any, Throwable, Option[Actor]] = {
    ZIO.fromFuture { implicit ec: ExecutionContext => schema.getActorById(id) }
  }
}

object ActorsRepositoryImpl {
  val live: ZLayer[ActorsSchema, Nothing, ActorsRepository] = ZLayer {
    for {
      schema <- ZIO.service[ActorsSchema]
    } yield new ActorsRepositoryImpl(schema)
  }
}
