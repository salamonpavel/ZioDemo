package com.github.salamonpavel.zio.service

import com.github.salamonpavel.zio.database.ActorsSchema
import com.github.salamonpavel.zio.model.Actor
import zio.{ZIO, ZLayer}

import scala.concurrent.ExecutionContext

/**
 *  A trait representing the repository for actors.
 */
trait ActorsRepository {

  /**
   *  Gets an actor by ID. This is an accessor method that requires an ActorsRepository.
   *
   *  @param id The ID of the actor.
   *  @return A ZIO effect that requires an ActorsRepository and produces an Option of Actor. The effect may fail with a Throwable if the ID is not valid.
   */
  def getActorById(id: Int): ZIO[Any, Throwable, Option[Actor]]
}

object ActorsRepository {

  /**
   *  Gets an actor by ID. This is an accessor method that requires an ActorsRepository.
   *
   *  @param id The ID of the actor.
   *  @return A ZIO effect that requires an ActorsRepository and produces an Option of Actor. The effect may fail with a Throwable if the ID is not valid.
   */
  def getActorById(id: Int): ZIO[ActorsRepository, Throwable, Option[Actor]] = {
    ZIO.serviceWithZIO[ActorsRepository](_.getActorById(id))
  }
}

/**
 *  An implementation of the ActorsRepository trait.
 */
class ActorsRepositoryImpl(schema: ActorsSchema) extends ActorsRepository {
  override def getActorById(id: Int): ZIO[Any, Throwable, Option[Actor]] = {
    ZIO.fromFuture { implicit ec: ExecutionContext => schema.getActorById(id) }
  }
}

object ActorsRepositoryImpl {

  /**
   *  A ZLayer that provides live implementation of ActorsRepository.
   */
  val live: ZLayer[ActorsSchema, Nothing, ActorsRepository] = ZLayer {
    for {
      schema <- ZIO.service[ActorsSchema]
    } yield new ActorsRepositoryImpl(schema)
  }
}
