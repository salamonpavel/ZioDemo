package com.github.salamonpavel.zio.util

import com.github.salamonpavel.zio.exception.ParseError
import zio.{ULayer, ZIO, ZLayer}
import zio.http.QueryParams

trait QueryParamsParser {
  def parseRequiredString(queryParams: QueryParams, param: String): ZIO[Any, ParseError, String]
}

object QueryParamsParser {
  def parseRequiredString(queryParams: QueryParams, param: String): ZIO[QueryParamsParser, ParseError, String] = {
    ZIO.serviceWithZIO[QueryParamsParser](_.parseRequiredString(queryParams, param))
  }
}

class QueryParamsParserImpl extends QueryParamsParser {
  override def parseRequiredString(queryParams: QueryParams, param: String): ZIO[Any, ParseError, String] = {
    ZIO.fromOption(queryParams.get(param)).foldZIO(
      _ => ZIO.fail(ParseError(s"Required parameter not provided: $param")),
      chunk => ZIO.succeed(chunk.asString)
    )
  }
}

object QueryParamsParserImpl {
  val live: ULayer[QueryParamsParser] = ZLayer.succeed(new QueryParamsParserImpl)
}
