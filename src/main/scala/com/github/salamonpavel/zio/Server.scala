package com.github.salamonpavel.zio

import cats.syntax.all._
import com.github.salamonpavel.zio.Constants.{ServerHost, ServerPort, SwaggerApiName, SwaggerApiVersion}
import com.github.salamonpavel.zio.controller._
import org.http4s.HttpRoutes
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.Router
import sttp.tapir.PublicEndpoint
import sttp.tapir.server.http4s.ztapir.ZHttp4sServerInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import sttp.tapir.ztapir._
import zio.interop.catz._
import zio.{RIO, ZIO}

trait Server extends ActorsRoutes with MoviesRoutes {

  type Env = ActorsController with MoviesController
  type F[A] = RIO[Env, A]

  private def createServerEndpoint[I, E, O](endpoint: PublicEndpoint[I, E, O, Any], logic: I => ZIO[Env, E, O]) =
    endpoint.zServerLogic(logic).widen[Env]

  // server routes  
  private def createAllServerRoutes: HttpRoutes[F] = {
    val endpoints = List(
      createServerEndpoint(getActorByIdEndpoint, ActorsController.findActorById),
      createServerEndpoint(getActorsEndpoint, ActorsController.findActors),
      createServerEndpoint(createActorEndpoint, ActorsController.createActor),
      createServerEndpoint(getMovieByIdEndpoint, MoviesController.findMovieById)
    )
    ZHttp4sServerInterpreter().from(endpoints).toRoutes
  }

  // swagger routes
  private def createSwaggerRoutes: HttpRoutes[F] = {
    val endpoints = List(getActorByIdEndpoint, getActorsEndpoint, createActorEndpoint, getMovieByIdEndpoint)
    ZHttp4sServerInterpreter()
      .from(SwaggerInterpreter().fromEndpoints[F](endpoints, SwaggerApiName, SwaggerApiVersion))
      .toRoutes
  }

  // http4s server
  protected val server: ZIO[Env, Throwable, Unit] =
    ZIO.executor.flatMap { executor =>
      BlazeServerBuilder[F]
        .withExecutionContext(executor.asExecutionContext)
        .bindHttp(ServerPort, ServerHost)
        .withHttpApp(Router("/" -> (createAllServerRoutes <+> createSwaggerRoutes)).orNotFound)
        .serve
        .compile
        .drain
    }
}
