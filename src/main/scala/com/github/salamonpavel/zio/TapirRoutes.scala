package com.github.salamonpavel.zio

import cats.syntax.all._
import com.github.salamonpavel.zio.Constants._
import com.github.salamonpavel.zio.controller.{ActorsController, ActorsControllerImpl}
import com.github.salamonpavel.zio.database.{CreateActor, GetActorById, GetActors, PostgresDatabaseProvider}
import com.github.salamonpavel.zio.http.{HttpRequestParserImpl, HttpResponseBuilderImpl}
import com.github.salamonpavel.zio.model.{Actor, ErrorApiResponse, SingleApiResponse}
import com.github.salamonpavel.zio.repository.ActorsRepositoryImpl
import com.github.salamonpavel.zio.service.ActorsServiceImpl
import org.http4s.HttpRoutes
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.Router
import sttp.tapir.PublicEndpoint
import sttp.tapir.generic.auto._
import sttp.tapir.json.play._
import sttp.tapir.server.http4s.ztapir.ZHttp4sServerInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import sttp.tapir.ztapir._
import zio.config.typesafe.TypesafeConfigProvider
import zio.interop.catz._
import zio.logging.consoleLogger
import zio.{Config, ConfigProvider, RIO, ZIO, ZIOAppDefault, ZLayer}

// https://github.com/softwaremill/tapir/blob/master/examples/src/main/scala/sttp/tapir/examples/ZioExampleHttp4sServer.scala

object TapirRoutes extends ZIOAppDefault {

  private val configProvider: ConfigProvider = TypesafeConfigProvider.fromResourcePath()

  // https://softwaremill.community/t/working-with-zio-and-http4s-given-a-non-empty-zio-environment/65
  type Env = ActorsController
  type F[A] = RIO[Env, A]

  // Define your endpoints using Tapir
  val getActorByIdEndpoint: PublicEndpoint[Int, ErrorApiResponse, SingleApiResponse[Actor], Any] =
    endpoint.get
      .name("getActorById")
      .in(Api / V1 / Actors / path[Int]("id"))
      .out(jsonBody[SingleApiResponse[Actor]])
      .errorOut(oneOf(oneOfVariant(jsonBody[ErrorApiResponse])))

  val actorsRoutes: HttpRoutes[F] = ZHttp4sServerInterpreter()
    .from(getActorByIdEndpoint.zServerLogic { id =>
      ActorsController.findActorById2(id)
    })
    .toRoutes

  val actorsServerEndpoint: ZServerEndpoint[ActorsController, Any] =
    getActorByIdEndpoint.zServerLogic { id =>
      ActorsController.findActorById2(id)
    }

  val actorsServerRoutes: HttpRoutes[F] = ZHttp4sServerInterpreter()
    .from(
      actorsServerEndpoint.widen[ActorsController]
    )
    .toRoutes

  // swagger routes
  val swaggerRoutes: HttpRoutes[F] =
    ZHttp4sServerInterpreter()
      .from(SwaggerInterpreter().fromEndpoints[F](List(getActorByIdEndpoint), "Actors/Movies API", "1.0"))
      .toRoutes

  private val allRoutes = actorsRoutes <+> swaggerRoutes

  // Starting the server
  val server: ZIO[ActorsController, Throwable, Unit] =
    ZIO.executor.flatMap(executor =>
      BlazeServerBuilder[F]
        .withExecutionContext(executor.asExecutionContext)
        .bindHttp(8080, "localhost")
        .withHttpApp(Router("/" -> allRoutes).orNotFound)
        .serve
        .compile
        .drain
    )

  override def run: ZIO[Any, Throwable, Unit] =
    server
      .provide(
        HttpRequestParserImpl.layer,
        HttpResponseBuilderImpl.layer,
        ActorsControllerImpl.layer,
        ActorsServiceImpl.layer,
        ActorsRepositoryImpl.layer,
        PostgresDatabaseProvider.layer,
        GetActorById.layer,
        GetActors.layer,
        CreateActor.layer
        //      ZLayer.Debug.mermaid,
      )

  override val bootstrap: ZLayer[Any, Config.Error, Unit] =
    zio.Runtime.removeDefaultLoggers >>> zio.Runtime.setConfigProvider(configProvider) >>> consoleLogger()

}
