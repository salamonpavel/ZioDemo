package com.github.salamonpavel.zio.http

import com.github.salamonpavel.zio.exception._
import play.api.libs.json.{Json, Reads}
import zio._
import zio.http.{QueryParams, Request}

import scala.util.Try

/**
 *  A trait representing a parser for query parameters.
 */
trait HttpRequestParser {

  /**
   *  Parses a required string parameter into an integer.
   *
   *  @param queryParams The query parameters to parse.
   *  @param param The name of the parameter to parse.
   *  @return A ZIO effect that produces an integer.
   *         The effect may fail with an AppError if the parameter is missing or not a valid integer.
   */
  def parseRequiredInt(queryParams: QueryParams, param: String): IO[AppError, Int]

  /**
   *  Parses the request body into an A.
   *
   *  @param request The request to parse.
   *  @return A ZIO effect that produces an A. The effect may fail with a RequestBodyError.
   */
  def parseRequestBody[A](request: Request)(implicit reads: Reads[A]): IO[RequestBodyError, A]

  /**
   *  Parses an optional string parameter.
   *
   *  @param queryParams The query parameters to parse.
   *  @param param The name of the parameter to parse.
   *  @return A ZIO effect that produces an optional string.
   */
  def getOptionalStringParam(queryParams: QueryParams, param: String): UIO[Option[String]]
}

/**
 *  An implementation of the QueryParamsParser trait.
 */
class HttpRequestParserImpl extends HttpRequestParser {

  /**
   *  Parses a required string parameter into an integer.
   */
  override def parseRequiredInt(
    queryParams: QueryParams,
    param: String
  ): IO[AppError, Int] = {
    for {
      paramStr <- ZIO
        .fromOption(queryParams.get(param))
        .mapError(_ => ParameterMissingError(s"The required parameter '$param' is missing."))
      paramInt <- ZIO
        .fromTry(Try(paramStr.asString.toInt))
        .mapError(_ => ParameterFormatError(s"The parameter '$param' is not a valid integer."))
    } yield paramInt
  }.tapError(error => ZIO.logError(s"Failed to parse required integer parameter due to: ${error.message}"))

  /**
   *  Parses an optional string parameter.
   */
  def getOptionalStringParam(queryParams: QueryParams, param: String): UIO[Option[String]] = {
    ZIO.succeed(queryParams.get(param).map(_.asString))
  }

  /**
   *  Parses the request body into an A.
   */
  def parseRequestBody[T](request: Request)(implicit reads: Reads[T]): IO[RequestBodyError, T] = {
    for {
      requestBody <- request.body.asString
        .mapError(error => RequestBodyError(s"The request body is invalid: $error"))
      parsedBody <- ZIO
        .fromEither(Json.parse(requestBody).validate[T].asEither)
        .mapError(error => RequestBodyError(s"The request body could not be parsed: $error"))
    } yield parsedBody
  }.tapError(error => ZIO.logError(s"Failed to parse request body due to: ${error.message}"))
}

object HttpRequestParserImpl {

  /**
   *  A ZLayer that provides a live implementation of HttpRequestParser.
   */
  val live: ULayer[HttpRequestParser] = ZLayer.succeed(new HttpRequestParserImpl)
}
