package com.github.salamonpavel.zio.repository

import com.github.salamonpavel.zio.database.{CreateActor, GetActorById, GetActors}
import com.github.salamonpavel.zio.exception.DatabaseError
import com.github.salamonpavel.zio.model.{Actor, CreateActorRequestBody, GetActorsQueryParameters}
import zio._

import scala.concurrent.ExecutionContext

/**
 *  A trait representing the repository for actors.
 */
trait ActorsRepository {

  /**
   *  Gets an actor by ID. This is an accessor method that requires an ActorsRepository.
   *
   *  @param id The ID of the actor.
   *  @return A ZIO effect that requires an ActorsRepository and produces an Option of Actor.
   *          The effect may fail with a DatabaseError.
   */
  def getActorById(id: Int): IO[DatabaseError, Option[Actor]]

  /**
   *  Gets actors by first name and/or last name.
   *
   *  @param requestParameters The query parameters to find actors.
   *  @return A ZIO effect that requires an ActorsRepository and produces a sequence of actors.
   *          The effect may fail with a DatabaseError.
   */
  def getActors(requestParameters: GetActorsQueryParameters): IO[DatabaseError, Seq[Actor]]

  /**
   *  Creates an actor. This is an accessor method that requires an ActorsRepository.
   *
   *  @param createActorRequestBody The request to create an actor.
   *  @return A ZIO effect that requires an ActorsRepository and produces an Actor.
   *          The effect may fail with a DatabaseError.
   */
  def createActor(createActorRequestBody: CreateActorRequestBody): IO[DatabaseError, Actor]
}

/**
 *  An implementation of the ActorsRepository trait.
 */
class ActorsRepositoryImpl(getActorByIdFn: GetActorById, getActorsFn: GetActors, createActorFn: CreateActor)
    extends ActorsRepository {

  /**
   *  Gets an actor by ID.
   */
  override def getActorById(id: Int): IO[DatabaseError, Option[Actor]] = {
    ZIO
      .fromFuture { implicit ec: ExecutionContext => getActorByIdFn(id) }
      .tap {
        case Some(actor) => ZIO.logInfo(s"Retrieved actor with ID $id: $actor")
        case None        => ZIO.logInfo(s"Actor with ID $id not found.")
      }
      .mapError(error => DatabaseError(error.getMessage))
      .tapError(error => ZIO.logError(s"Failed to retrieve actor with ID $id: ${error.message}"))
  }

  /**
   *  Gets actors by first name and/or last name.
   */
  override def getActors(requestParameters: GetActorsQueryParameters): IO[DatabaseError, Seq[Actor]] = {
    ZIO
      .fromFuture { implicit ec: ExecutionContext => getActorsFn(requestParameters) }
      .tap { actors =>
        ZIO.logInfo(
          s"Retrieved ${actors.size} actor(s) with first name: ${requestParameters.firstName} " +
            s"and last name: ${requestParameters.lastName}."
        )
      }
      .mapError(error => DatabaseError(error.getMessage))
      .tapError(error =>
        ZIO.logError(
          s"Failed to retrieve actors with first name: ${requestParameters.firstName} " +
            s"and last name: ${requestParameters.lastName}: ${error.message}"
        )
      )
  }

  /**
   *  Creates an actor.
   */
  override def createActor(createActorRequestBody: CreateActorRequestBody): IO[DatabaseError, Actor] = {
    ZIO
      .fromFuture { implicit ec: ExecutionContext => createActorFn(createActorRequestBody) }
      .map(id => Actor(id, createActorRequestBody.firstName, createActorRequestBody.lastName))
      .tap(actor =>
        ZIO.logInfo(
          s"Created actor with ID ${actor.actorId}, first name: ${actor.firstName}, " +
            s"and last name: ${actor.lastName}."
        )
      )
      .mapError(error => DatabaseError(error.getMessage))
      .tapError(error => ZIO.logError(s"Failed to create actor: ${error.message}"))
  }
}

object ActorsRepositoryImpl {

  /**
   *  A ZLayer that provides live implementation of ActorsRepository.
   */
  val layer: URLayer[GetActorById with GetActors with CreateActor, ActorsRepository] = ZLayer {
    for {
      getActorByIdFn <- ZIO.service[GetActorById]
      getActorsFn    <- ZIO.service[GetActors]
      createActorFn  <- ZIO.service[CreateActor]
    } yield new ActorsRepositoryImpl(getActorByIdFn, getActorsFn, createActorFn)
  }
}
