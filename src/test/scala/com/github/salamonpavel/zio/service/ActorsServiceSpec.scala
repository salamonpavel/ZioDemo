package com.github.salamonpavel.zio.service

import com.github.salamonpavel.zio.exception.{DatabaseError, ServiceError}
import com.github.salamonpavel.zio.model.{Actor, CreateActorRequestBody, GetActorsParams}
import com.github.salamonpavel.zio.repository.ActorsRepository
import org.junit.runner.RunWith
import zio._
import zio.test.Assertion._
import zio.test._
import zio.test.junit.ZTestJUnitRunner

@RunWith(classOf[ZTestJUnitRunner])
class ActorsServiceSpec extends ZIOSpecDefault {

  class ActorsRepositoryFake extends ActorsRepository {

    override def getActorById(id: Int): IO[DatabaseError, Option[Actor]] = {
      if (id == 1) ZIO.succeed(Some(Actor(1, "John", "Newman")))
      else if (id == 2) ZIO.succeed(None)
      else ZIO.fail(DatabaseError("an error"))
    }

    override def getActors(params: GetActorsParams): IO[DatabaseError, Seq[Actor]] = {
      params.firstName match {
        case Some(value) =>
          if (value == "John") ZIO.succeed(Seq(Actor(1, "John", "Newman")))
          else ZIO.succeed(Seq.empty)
        case None =>
          ZIO.fail(DatabaseError("an error"))
      }
    }

    override def createActor(requestBody: CreateActorRequestBody): IO[DatabaseError, Actor] = {
      if (requestBody.firstName == "John") ZIO.succeed(Actor(1, "John", "Newman"))
      else ZIO.fail(DatabaseError("an error"))
    }
  }

  private val actorsRepositoryLayer = ZLayer.succeed(new ActorsRepositoryFake)

  override def spec: Spec[TestEnvironment with Scope, Any] = suite("ActorsService") (

    suite("findActorById") (

      test("returns an expected instance of Actor") {
        val expectedActor = Actor(1, "John", "Newman")
        for {
          actor <- ActorsService.findActorById(1)
        } yield assertTrue(actor.contains(expectedActor))
      },

      test("returns an expected None") {
        val expectedActor = Option.empty[Actor]
        for {
          actor <- ActorsService.findActorById(2)
        } yield assertTrue(actor == expectedActor)
      },

      test("returns an expected ServiceError") {
        assertZIO(ActorsService.findActorById(3).exit)(failsWithA[ServiceError])
      }
    ),

    suite("getActors") (

      test("returns expected Seq of Actors") {
        val expectedActors = Seq(Actor(1, "John", "Newman"))
        for {
          actors <- ActorsService.findActors(GetActorsParams(Some("John"), None))
        } yield assertTrue(actors == expectedActors)
      },

      test("returns expected empty Seq of Actors") {
        val expectedActors = Seq.empty[Actor]
        for {
          actors <- ActorsService.findActors(GetActorsParams(Some("William"), None))
        } yield assertTrue(actors == expectedActors)
      },

      test("returns an expected ServiceError") {
        assertZIO(ActorsService.findActors(GetActorsParams(None, None)).exit)(failsWithA[ServiceError])
      }
    ),

    suite("createActor")(

      test("returns an expected instance of Actor") {
        val expectedActor = Actor(1, "John", "Newman")
        for {
          actor <- ActorsService.createActor(CreateActorRequestBody("John", "Newman"))
        } yield assertTrue(actor == expectedActor)
      },

      test("returns an expected ServiceError") {
        assertZIO(ActorsService.createActor(CreateActorRequestBody("William", "Newman")).exit)(failsWithA[ServiceError])
      }

    )

  ).provide(ActorsServiceImpl.layer, actorsRepositoryLayer)

}