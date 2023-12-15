package com.github.salamonpavel.zio.service

import com.github.salamonpavel.zio.exception.{DatabaseError, ServiceError}
import com.github.salamonpavel.zio.model.{Actor, CreateActorRequestBody, GetActorsQueryParameters}
import com.github.salamonpavel.zio.repository.ActorsRepository
import zio._
import zio.macros.accessible

/**
 *  A trait representing the service for actors.
 */
@accessible
trait ActorsService {

  /**
   *  Finds an actor by ID.
   *
   *  @param id The ID of the actor.
   *  @return A ZIO effect that produces an Option of Actor. The effect may fail with a ServiceError.
   */
  def findActorById(id: Int): IO[ServiceError, Option[Actor]]

  /**
   *  Finds actors by first name and/or last name.
   *
   *  @param requestParameters The query parameters to find actors.
   *  @return A ZIO effect that produces a sequence of actors. The effect may fail with a ServiceError.
   */
  def findActors(requestParameters: GetActorsQueryParameters): IO[ServiceError, Seq[Actor]]

  /**
   *  Creates an actor.
   *
   *  @param createActorRequestBody The request to create an actor.
   *  @return A ZIO effect that produces an Actor. The effect may fail with a ServiceError.
   */
  def createActor(createActorRequestBody: CreateActorRequestBody): IO[ServiceError, Actor]
}

/**
 *  An implementation of the ActorsService trait.
 *
 *  @param actorsRepository The ActorsRepository that this service will use to interact with the database.
 */
class ActorsServiceImpl(actorsRepository: ActorsRepository) extends ActorsService {

  /**
   *  Finds an actor by ID.
   */
  override def findActorById(id: Int): IO[ServiceError, Option[Actor]] = {
    actorsRepository.getActorById(id).mapError { case DatabaseError(message) =>
      ServiceError(s"Failed to find actor by id: $message")
    }
  }

  /**
   *  Finds actors by first name and/or last name.
   */
  override def findActors(requestParameters: GetActorsQueryParameters): IO[ServiceError, Seq[Actor]] = {
    actorsRepository.getActors(requestParameters).mapError { case DatabaseError(message) =>
      ServiceError(s"Failed to find actors: $message")
    }
  }

  /**
   *  Creates an actor.
   */
  override def createActor(createActorRequestBody: CreateActorRequestBody): IO[ServiceError, Actor] = {
    actorsRepository.createActor(createActorRequestBody).mapError { case DatabaseError(message) =>
      ServiceError(s"Failed to create actor: $message")
    }
  }
}

object ActorsServiceImpl {

  /**
   *  A ZLayer that provides a live implementation of ActorsService.
   */
  val layer: URLayer[ActorsRepository, ActorsService] =
    ZLayer {
      for {
        actorsRepository <- ZIO.service[ActorsRepository]
      } yield new ActorsServiceImpl(actorsRepository)
    }
}
