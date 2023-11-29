package com.github.salamonpavel.zio.http

import com.github.salamonpavel.zio.exception._
import zio._
import zio.http.{QueryParams, Request}
import zio.json.{DecoderOps, JsonDecoder}

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
  def parseRequestBody[A](request: Request)(implicit decoder: JsonDecoder[A]): IO[RequestBodyError, A]
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
        .tapError(error => ZIO.logError(s"Failed to parse required integer parameter due to: ${error.message}"))
      paramInt <- ZIO
        .fromTry(Try(paramStr.asString.toInt))
        .mapError(_ => ParameterFormatError(s"The parameter '$param' is not a valid integer."))
        .tapError(error => ZIO.logError(s"Failed to parse required integer parameter due to: ${error.message}"))
    } yield paramInt
  }

  /**
   *  Parses the request body into an A.
   */
  def parseRequestBody[A](request: Request)(implicit decoder: JsonDecoder[A]): IO[RequestBodyError, A] = {
    for {
      requestBody <- request.body.asString
        .mapError(error => RequestBodyError(s"The request body is invalid: $error"))
        .tapError(error => ZIO.logError(s"Failed to parse request body due to: ${error.message}"))
      parsedBody <- ZIO
        .fromEither(requestBody.fromJson[A])
        .mapError(error => RequestBodyError(s"The request body could not be parsed: $error"))
        .tapError(error => ZIO.logError(s"Failed to parse request body due to: ${error.message}"))
    } yield parsedBody
  }
}

object HttpRequestParserImpl {

  /**
   *  A ZLayer that provides a live implementation of HttpRequestParser.
   */
  val live: ULayer[HttpRequestParser] = ZLayer.succeed(new HttpRequestParserImpl)
}
