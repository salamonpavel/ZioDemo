package com.github.salamonpavel.zio.util

import com.github.salamonpavel.zio.exception.{AppError, ParameterFormatError, ParameterMissingError}
import zio.{ULayer, ZIO, ZLayer}
import zio.http.QueryParams

import scala.util.Try

/**
 *  A trait representing a parser for query parameters.
 */
trait QueryParamsParser {

  /**
   *  Parses a required string parameter into an integer.
   *
   *  @param queryParams The query parameters to parse.
   *  @param param The name of the parameter to parse.
   *  @return A ZIO effect that produces an integer.
   *         The effect may fail with an AppError if the parameter is missing or not a valid integer.
   */
  def parseRequiredInt(queryParams: QueryParams, param: String): ZIO[Any, AppError, Int]
}

object QueryParamsParser {

  /**
   *  Parses a required string parameter into an integer. This is an accessor method that requires a QueryParamsParser.
   *
   *  @param queryParams The query parameters to parse.
   *  @param param The name of the parameter to parse.
   *  @return A ZIO effect that requires a QueryParamsParser and produces an integer.
   *         The effect may fail with an AppError if the parameter is missing or not a valid integer.
   */
  def parseRequiredInt(
    queryParams: QueryParams,
    param: String
  ): ZIO[QueryParamsParser, AppError, Int] = {
    ZIO.serviceWithZIO[QueryParamsParser](_.parseRequiredInt(queryParams, param))
  }
}

/**
 *  An implementation of the QueryParamsParser trait.
 */
class QueryParamsParserImpl extends QueryParamsParser {

  /**
   *  Parses a required string parameter into an integer.
   *
   *  @param queryParams The query parameters to parse.
   *  @param param The name of the parameter to parse.
   *  @return A ZIO effect that produces an integer.
   *         The effect may fail with a ParameterMissingError or a ParameterFormatError.
   */
  override def parseRequiredInt(
    queryParams: QueryParams,
    param: String
  ): ZIO[Any, AppError, Int] = {
    for {
      paramStr <- ZIO
        .fromOption(queryParams.get(param))
        .mapError(_ => ParameterMissingError(s"Missing parameter: $param"))
        .tapError(error => ZIO.logError(s"Failed to parse required integer parameter: ${error.message}"))
      paramInt <- ZIO
        .fromTry(Try(paramStr.asString.toInt))
        .mapError(_ => ParameterFormatError(s"Invalid integer parameter: $paramStr"))
        .tapError(error => ZIO.logError(s"Failed to parse required integer parameter: ${error.message}"))
    } yield paramInt
  }
}

object QueryParamsParserImpl {

  /**
   *  A ZLayer that provides a live implementation of QueryParamsParser.
   */
  val live: ULayer[QueryParamsParser] = ZLayer.succeed(new QueryParamsParserImpl)
}
