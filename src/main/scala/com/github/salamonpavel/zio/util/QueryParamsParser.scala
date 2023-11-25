package com.github.salamonpavel.zio.util

import com.github.salamonpavel.zio.exception.{AppError, ParameterNumberFormatError, RequiredParameterMissingError}
import zio.{ULayer, ZIO, ZLayer}
import zio.http.QueryParams

import scala.util.Try

trait QueryParamsParser {
  def parseRequiredStringIntoInt(queryParams: QueryParams, param: String): ZIO[Any, AppError, Int]
}

object QueryParamsParser {
  def parseRequiredStringIntoInt(
                                  queryParams: QueryParams,
                                  param: String
                                ): ZIO[QueryParamsParser, AppError, Int] = {
    ZIO.serviceWithZIO[QueryParamsParser](_.parseRequiredStringIntoInt(queryParams, param))
  }
}

class QueryParamsParserImpl extends QueryParamsParser {
  override def parseRequiredStringIntoInt(
                                           queryParams: QueryParams,
                                           param: String
                                         ): ZIO[Any, AppError, Int] = {
    for {
      paramStr <- ZIO.fromOption(queryParams.get(param))
        .mapError(_ => RequiredParameterMissingError(s"Missing parameter: $param"))
      paramInt <- ZIO.fromTry(Try(paramStr.asString.toInt))
        .mapError(_ => ParameterNumberFormatError(s"Invalid integer parameter: $paramStr"))
    } yield paramInt
  }
}

object QueryParamsParserImpl {
  val live: ULayer[QueryParamsParser] = ZLayer.succeed(new QueryParamsParserImpl)
}
