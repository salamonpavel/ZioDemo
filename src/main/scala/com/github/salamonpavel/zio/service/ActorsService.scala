package com.github.salamonpavel.zio.service

import com.github.salamonpavel.zio.exception.DatabaseError
import com.github.salamonpavel.zio.model.Actor
import com.github.salamonpavel.zio.repository.ActorsRepository
import zio.{ZIO, ZLayer}

/**
 *  A trait representing the service for actors.
 */
trait ActorsService {

  /**
   *  Finds an actor by ID.
   *
   *  @param id The ID of the actor.
   *  @return A ZIO effect that produces an Option of Actor. The effect may fail with a DatabaseError.
   */
  def findActorById(id: Int): ZIO[Any, DatabaseError, Option[Actor]]
}

object ActorsService {

  /**
   *  Finds an actor by ID. This is an accessor method that requires an ActorsService.
   *
   *  @param id The ID of the actor.
   *  @return A ZIO effect that requires an ActorsService and produces an Option of Actor.
   *         The effect may fail with a DatabaseError.
   */
  def findActorById(id: Int): ZIO[ActorsService, DatabaseError, Option[Actor]] = {
    ZIO.serviceWithZIO[ActorsService](_.findActorById(id))
  }
}

/**
 *  An implementation of the ActorsService trait.
 *
 *  @param actorsRepository The ActorsRepository that this service will use to interact with the database.
 */
class ActorsServiceImpl(actorsRepository: ActorsRepository) extends ActorsService {

  /**
   *  Finds an actor by ID.
   *
   *  @param id The ID of the actor.
   *  @return A ZIO effect that produces an Option of Actor. The effect may fail with a DatabaseError.
   */
  override def findActorById(id: Int): ZIO[Any, DatabaseError, Option[Actor]] = {
    actorsRepository.getActorById(id)
  }
}

object ActorsServiceImpl {

  /**
   *  A ZLayer that provides a live implementation of ActorsService.
   */
  val live: ZLayer[ActorsRepository, Nothing, ActorsService] =
    ZLayer {
      for {
        actorsRepository <- ZIO.service[ActorsRepository]
      } yield new ActorsServiceImpl(actorsRepository)
    }
}
