package com.github.salamonpavel.zio.service

import com.github.salamonpavel.zio.model.Actor
import zio.{ZIO, ZLayer}

trait ActorsService {
  def findActorById(id: Int): ZIO[Any, Throwable, Option[Actor]]
}

object ActorsService {
  def findActorById(id: Int): ZIO[ActorsService, Throwable, Option[Actor]] = {
    ZIO.serviceWithZIO[ActorsService](_.findActorById(id))
  }
}

class ActorsServiceImpl(actorsRepository: ActorsRepository) extends ActorsService {
  override def findActorById(id: Int): ZIO[Any, Throwable, Option[Actor]] = {
    for {
      actor <- actorsRepository.getActorById(id)
    } yield actor
  }
}

object ActorsServiceImpl {
  val live: ZLayer[ActorsRepository, Nothing, ActorsService] =
    ZLayer {
      for {
        actorsRepository <- ZIO.service[ActorsRepository]
      } yield new ActorsServiceImpl(actorsRepository)
    }
}