package com.github.salamonpavel.zio.service

import com.github.salamonpavel.zio.exception.DatabaseError
import com.github.salamonpavel.zio.model.{Actor, CreateActorRequestBody}
import com.github.salamonpavel.zio.repository.ActorsRepository
import zio._

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
  def findActorById(id: Int): IO[DatabaseError, Option[Actor]]

  /**
   *  Creates an actor.
   *
   *  @param createActorRequestBody The request to create an actor.
   *  @return A ZIO effect that produces an Actor. The effect may fail with a DatabaseError.
   */
  def createActor(createActorRequestBody: CreateActorRequestBody): IO[DatabaseError, Unit]
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
  override def findActorById(id: Int): IO[DatabaseError, Option[Actor]] = {
    for {
      _     <- ZIO.logDebug("Trying to find an actor by ID.")
      actor <- actorsRepository.getActorById(id)
    } yield actor
  }

  /**
   *  Creates an actor.
   *
   *  @param createActorRequestBody The request to create an actor.
   *  @return A ZIO effect that produces an Actor. The effect may fail with a DatabaseError.
   */
  override def createActor(createActorRequestBody: CreateActorRequestBody): IO[DatabaseError, Unit] = {
    for {
      _ <- ZIO.logDebug(
        s"Trying to create an actor with first name ${createActorRequestBody.firstName} and last name ${createActorRequestBody.lastName}."
      )
      actor <- actorsRepository.createActor(createActorRequestBody)
    } yield actor
  }
}

object ActorsServiceImpl {

  /**
   *  A ZLayer that provides a live implementation of ActorsService.
   */
  val live: URLayer[ActorsRepository, ActorsService] =
    ZLayer {
      for {
        actorsRepository <- ZIO.service[ActorsRepository]
      } yield new ActorsServiceImpl(actorsRepository)
    }
}
