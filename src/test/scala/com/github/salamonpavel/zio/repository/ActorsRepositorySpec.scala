package com.github.salamonpavel.zio.repository

import com.github.salamonpavel.zio.database.{CreateActor, GetActorById, GetActors, PostgresDatabaseProvider, TransactorProvider}
import com.github.salamonpavel.zio.exception.DatabaseError
import com.github.salamonpavel.zio.model.{Actor, CreateActorRequestBody, GetActorsParams}
import org.junit.runner.RunWith
import org.mockito.Mockito.{mock, when}
import zio.config.typesafe.TypesafeConfigProvider
import zio.test.Assertion.failsWithA
import zio.test.junit.ZTestJUnitRunner
import zio.test.{Spec, TestEnvironment, ZIOSpec, ZIOSpecDefault, assertTrue, assertZIO}
import zio._

@RunWith(classOf[ZTestJUnitRunner])
class ActorsRepositorySpec extends ZIOSpecDefault {

  private val getActorByIdMock: GetActorById = mock(classOf[GetActorById])
  private val getActorsMock: GetActors = mock(classOf[GetActors])
  private val createActorMock: CreateActor = mock(classOf[CreateActor])

  when(getActorByIdMock.apply(1)).thenReturn(ZIO.some(Actor(1, "John", "Newman")))
  when(getActorByIdMock.apply(2)).thenReturn(ZIO.none)
  when(getActorByIdMock.apply(3)).thenReturn(ZIO.fail(new RuntimeException("an error")))

  when(getActorsMock.apply(GetActorsParams(Some("John"), Some("Newman"))))
    .thenReturn(ZIO.succeed(Seq(Actor(1, "John", "Newman"))))
  when(getActorsMock.apply(GetActorsParams(None, None)))
    .thenReturn(ZIO.fail(new RuntimeException("an error")))

  when(createActorMock.apply(CreateActorRequestBody("John", "Newman"))).thenReturn(ZIO.succeed(1))
  when(createActorMock.apply(CreateActorRequestBody("John", "Doe")))
    .thenReturn(ZIO.fail(new RuntimeException("an error")))

  private val getActorByIdLayer: ULayer[GetActorById] = ZLayer.succeed(getActorByIdMock)
  private val getActorsLayer: ULayer[GetActors] = ZLayer.succeed(getActorsMock)
  private val createActorLayer: ULayer[CreateActor] = ZLayer.succeed(createActorMock)

  val testConfigProvider: ZLayer[Any, Nothing, Unit] =
    Runtime.setConfigProvider(TypesafeConfigProvider.fromResourcePath())

  override def spec: Spec[TestEnvironment with Scope, Any] = {

    suite("ActorsRepository")(
      suite("getActorById")(

        test("returns an expected instance of Actor from postgres in docker container") {
          val expectedActor = Actor(1, "John", "Doe")
          for {
            actor <- ActorsRepository.getActorById(1)
          } yield assertTrue(actor.contains(expectedActor))
        }.provide(
          ActorsRepositoryImpl.layer,
          PostgresDatabaseProvider.layer,
          TransactorProvider.layer,
          GetActorById.layer,
          GetActors.layer,
          CreateActor.layer,
          zio.Scope.default
        ),

        test("returns an expected instance of Actor") {
          val expectedActor = Actor(1, "John", "Newman")
          for {
            actor <- ActorsRepository.getActorById(1)
          } yield assertTrue(actor.contains(expectedActor))
        },
        test("returns an expected None") {
          val expectedActor = Option.empty[Actor]
          for {
            actor <- ActorsRepository.getActorById(2)
          } yield assertTrue(actor == expectedActor)
        },
        test("returns an expected DatabaseError") {
          assertZIO(ActorsRepository.getActorById(3).exit)(failsWithA[DatabaseError])
        }
      ),
      suite("getActors")(
        test("returns expected Seq of Actors") {
          val expectedActors = Seq(Actor(1, "John", "Newman"))
          for {
            actors <- ActorsRepository.getActors(GetActorsParams(Some("John"), Some("Newman")))
          } yield assertTrue(actors == expectedActors)
        },
        test("returns an expected DatabaseError") {
          assertZIO(ActorsRepository.getActors(GetActorsParams(None, None)).exit)(failsWithA[DatabaseError])
        }
      ),
      suite("createActor")(
        test("returns an expected instance of Actor") {
          val expectedActor = Actor(1, "John", "Newman")
          for {
            actor <- ActorsRepository.createActor(CreateActorRequestBody("John", "Newman"))
          } yield assertTrue(actor == expectedActor)
        },
        test("returns an expected DatabaseError") {
          assertZIO(ActorsRepository.createActor(CreateActorRequestBody("John", "Doe")).exit)(failsWithA[DatabaseError])
        }
      )
    ).provide(ActorsRepositoryImpl.layer, getActorByIdLayer, getActorsLayer, createActorLayer)

  }.provideLayer(testConfigProvider)

}
