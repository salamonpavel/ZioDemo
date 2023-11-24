package com.github.salamonpavel.zio.service

import com.github.salamonpavel.zio.database.Runs
import com.github.salamonpavel.zio.model.Actor
import zio.{ZIO, ZLayer}

trait ActorsRepository {
  def getActorById(id: Int): ZIO[Any, Throwable, Option[Actor]]
}

class ActorsRepositoryImpl(runs: Runs) extends ActorsRepository {
  override def getActorById(id: Int): ZIO[Any, Throwable, Option[Actor]] = {
    ???
  }
}


object ActorsRepositoryImpl {
  val live: ZLayer[Runs, Nothing, ActorsRepository] =
    ???
}
