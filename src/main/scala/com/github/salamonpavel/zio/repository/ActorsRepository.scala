package com.github.salamonpavel.zio.repository

import com.github.salamonpavel.zio.database.ActorsSchema
import com.github.salamonpavel.zio.exception.DatabaseError
import com.github.salamonpavel.zio.model.{Actor, CreateActorRequestBody}
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
   *  Creates an actor. This is an accessor method that requires an ActorsRepository.
   *
   *  @param createActorRequestBody The request to create an actor.
   *  @return A ZIO effect that requires an ActorsRepository and produces an Actor.
   *          The effect may fail with a DatabaseError.
   */
  def createActor(createActorRequestBody: CreateActorRequestBody): IO[DatabaseError, Unit]
}

/**
 *  An implementation of the ActorsRepository trait.
 */
class ActorsRepositoryImpl(schema: ActorsSchema) extends ActorsRepository {

  /**
   *  Gets an actor by ID.
   */
  override def getActorById(id: Int): IO[DatabaseError, Option[Actor]] = {
    ZIO
      .fromFuture { implicit ec: ExecutionContext => schema.getActorById(id) }
      .tap {
        case Some(actor) => ZIO.logInfo(s"Retrieved actor with ID $id: $actor")
        case None        => ZIO.logInfo(s"Actor with ID $id not found.")
      }
      .mapError(error => DatabaseError(error.getMessage))
      .tapError(error => ZIO.logError(s"Failed to retrieve actor with ID $id: ${error.message}"))
  }

  /**
   *  Creates an actor.
   */
  override def createActor(createActorRequestBody: CreateActorRequestBody): IO[DatabaseError, Unit] = {
    ZIO
      .fromFuture { implicit ec: ExecutionContext => schema.createActor(createActorRequestBody) }
      .tap(_ =>
        ZIO.logInfo(
          s"Created actor with first name: ${createActorRequestBody.firstName} " +
            s"and last name: ${createActorRequestBody.lastName}."
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
  val live: URLayer[ActorsSchema, ActorsRepository] = ZLayer {
    for {
      schema <- ZIO.service[ActorsSchema]
    } yield new ActorsRepositoryImpl(schema)
  }
}
