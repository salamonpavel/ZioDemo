package com.github.salamonpavel.zio.repository

import com.github.salamonpavel.zio.database.{CreateActor, GetActorById, GetActors}
import com.github.salamonpavel.zio.exception.DatabaseError
import com.github.salamonpavel.zio.model.{Actor, CreateActorRequestBody, GetActorsParams}
import org.junit.runner.RunWith
import org.mockito.Mockito.{mock, when}
import zio.test.Assertion.failsWithA
import zio.test.junit.ZTestJUnitRunner
import zio.test.{Spec, TestEnvironment, ZIOSpecDefault, assertTrue, assertZIO}
import zio.{Scope, ULayer, ZLayer}

import scala.concurrent.Future

@RunWith(classOf[ZTestJUnitRunner])
class ActorsRepositorySpec extends ZIOSpecDefault {

  private val getActorByIdMock: GetActorById = mock(classOf[GetActorById])
  private val getActorsMock: GetActors = mock(classOf[GetActors])
  private val createActorMock: CreateActor = mock(classOf[CreateActor])

  when(getActorByIdMock.apply(1)).thenReturn(Future.successful(Some(Actor(1, "John", "Newman"))))
  when(getActorByIdMock.apply(2)).thenReturn(Future.successful(None))
  when(getActorByIdMock.apply(3)).thenThrow(new RuntimeException("an error"))

  when(getActorsMock.apply(GetActorsParams(Some("John"), Some("Newman"))))
    .thenReturn(Future.successful(Seq(Actor(1, "John", "Newman"))))
  when(getActorsMock.apply(GetActorsParams(None, None)))
    .thenThrow(new RuntimeException("an error"))

  when(createActorMock.apply(CreateActorRequestBody("John", "Newman"))).thenReturn(Future.successful(1))
  when(createActorMock.apply(CreateActorRequestBody("John", "Doe"))).thenThrow(new RuntimeException("an error"))

  private val getActorByIdLayer: ULayer[GetActorById] = ZLayer.succeed(getActorByIdMock)
  private val getActorsLayer: ULayer[GetActors] = ZLayer.succeed(getActorsMock)
  private val createActorLayer: ULayer[CreateActor] = ZLayer.succeed(createActorMock)

  override def spec: Spec[TestEnvironment with Scope, Any] = suite("ActorsRepository")(

    suite("getActorById")(

      test("returns an expected instance of Actor"){
        val expectedActor = Actor(1, "John", "Newman")
        for {
          actor <- ActorsRepository.getActorById(1)
        } yield assertTrue(actor.contains(expectedActor))
      },

      test("returns an expected None"){
        val expectedActor = Option.empty[Actor]
        for {
          actor <- ActorsRepository.getActorById(2)
        } yield assertTrue(actor == expectedActor)
      },

      test("returns an expected DatabaseError"){
        assertZIO(ActorsRepository.getActorById(3).exit)(failsWithA[DatabaseError])
      }
    ),

    suite("getActors")(

      test("returns expected Seq of Actors"){
        val expectedActors = Seq(Actor(1, "John", "Newman"))
        for {
          actors <- ActorsRepository.getActors(GetActorsParams(Some("John"), Some("Newman")))
        } yield assertTrue(actors == expectedActors)
      },

      test("returns an expected DatabaseError"){
        assertZIO(ActorsRepository.getActors(GetActorsParams(None, None)).exit)(failsWithA[DatabaseError])
      }
    ),

    suite("createActor")(

      test("returns an expected instance of Actor"){
        val expectedActor = Actor(1, "John", "Newman")
        for {
          actor <- ActorsRepository.createActor(CreateActorRequestBody("John", "Newman"))
        } yield assertTrue(actor == expectedActor)
      },

      test("returns an expected DatabaseError"){
        assertZIO(ActorsRepository.createActor(CreateActorRequestBody("John", "Doe")).exit)(failsWithA[DatabaseError])
      }
    )

  ).provide(ActorsRepositoryImpl.layer, getActorByIdLayer, getActorsLayer, createActorLayer)

}
