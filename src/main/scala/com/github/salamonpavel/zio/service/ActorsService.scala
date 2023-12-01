package com.github.salamonpavel.zio.service

import com.github.salamonpavel.zio.exception.DatabaseError
import com.github.salamonpavel.zio.model.{Actor, CreateActorRequestBody, GetActorsQueryParameters}
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

  def findActors(requestParameters: GetActorsQueryParameters): IO[DatabaseError, Seq[Actor]]

  /**
   *  Creates an actor.
   *
   *  @param createActorRequestBody The request to create an actor.
   *  @return A ZIO effect that produces an Actor. The effect may fail with a DatabaseError.
   */
  def createActor(createActorRequestBody: CreateActorRequestBody): IO[DatabaseError, Actor]
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
  override def findActorById(id: Int): IO[DatabaseError, Option[Actor]] = {
    actorsRepository.getActorById(id)
  }

  /**
   *  Finds actors by first name and/or last name.
   */
  override def findActors(requestParameters: GetActorsQueryParameters): IO[DatabaseError, Seq[Actor]] = {
    actorsRepository.getActors(requestParameters)
  }

  /**
   *  Creates an actor.
   */
  override def createActor(createActorRequestBody: CreateActorRequestBody): IO[DatabaseError, Actor] = {
    actorsRepository.createActor(createActorRequestBody)
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
