package com.github.salamonpavel.zio

import cats.syntax.all._
import com.github.salamonpavel.zio.Constants._
import com.github.salamonpavel.zio.controller._
import com.github.salamonpavel.zio.model.{ApiResponseStatus, ErrorApiResponse}
import org.http4s.HttpRoutes
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.Router
import sttp.monad.MonadError
import sttp.tapir.generic.auto.schemaForCaseClass
import sttp.tapir.json.play.jsonBody
import sttp.tapir.server.http4s.Http4sServerOptions
import sttp.tapir.server.http4s.ztapir.ZHttp4sServerInterpreter
import sttp.tapir.server.interceptor.DecodeFailureContext
import sttp.tapir.server.interceptor.decodefailure.DecodeFailureHandler
import sttp.tapir.server.interceptor.decodefailure.DefaultDecodeFailureHandler.respond
import sttp.tapir.server.model.ValuedEndpointOutput
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import sttp.tapir.ztapir._
import sttp.tapir.{DecodeResult, PublicEndpoint, headers, statusCode}
import zio.interop.catz._
import zio.{RIO, ZIO}

trait Server extends Endpoints {

  type Env = ActorsController with MoviesController
  type F[A] = RIO[Env, A]

  private val decodeFailureHandler: DecodeFailureHandler[F] = new DecodeFailureHandler[F] {
    override def apply(ctx: DecodeFailureContext)(implicit monad: MonadError[F]): F[Option[ValuedEndpointOutput[_]]] = {
      monad.unit(
        respond(ctx).map { case (sc, hs) =>
          val message = ctx.failure match {
            case DecodeResult.Missing =>
              s"Decoding error - missing value."
            case DecodeResult.Multiple(vs) =>
              s"Decoding error - $vs."
            case DecodeResult.Error(original, _) =>
              s"Decoding error for an input value '$original'."
            case DecodeResult.Mismatch(_, actual) =>
              s"Unexpected value '$actual'."
            case DecodeResult.InvalidValue(errors) =>
              s"Validation error - $errors."
          }
          val errorResponse = ErrorApiResponse(ApiResponseStatus.BadRequest, message)
          ValuedEndpointOutput(statusCode.and(headers).and(jsonBody[ErrorApiResponse]), (sc, hs, errorResponse))
        }
      )
    }
  }

  private val http4sServerOptions: Http4sServerOptions[F] = Http4sServerOptions
    .customiseInterceptors[F]
    .decodeFailureHandler(decodeFailureHandler)
    .options

  private def createServerEndpoint[I, E, O](
    endpoint: PublicEndpoint[I, E, O, Any],
    logic: I => ZIO[Env, E, O]
  ): ZServerEndpoint[Env, Any] =
    endpoint.zServerLogic(logic).widen[Env]

  // server routes
  private def createAllServerRoutes: HttpRoutes[F] = {
    val endpoints = List(
      createServerEndpoint(getActorByIdEndpoint, ActorsController.findActorById),
      createServerEndpoint(getActorsEndpoint, ActorsController.findActors),
      createServerEndpoint(createActorEndpoint, ActorsController.createActor),
      createServerEndpoint(getMovieByIdEndpoint, MoviesController.findMovieById)
    )
    ZHttp4sServerInterpreter[Env](http4sServerOptions).from(endpoints).toRoutes
  }

  // swagger routes
  private def createSwaggerRoutes: HttpRoutes[F] = {
    val endpoints = List(getActorByIdEndpoint, getActorsEndpoint, createActorEndpoint, getMovieByIdEndpoint)
    ZHttp4sServerInterpreter[Env](http4sServerOptions)
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
