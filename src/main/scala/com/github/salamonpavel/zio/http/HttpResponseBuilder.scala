package com.github.salamonpavel.zio.http

import play.api.libs.json.{Json, Writes}
import zio._
import zio.http.Response
import zio.http.model.Status

trait HttpResponseBuilder {

  /**
   *  Converts an Option of T to a Response.
   *
   *  @param option The Option of T.
   *  @return A Response.
   */
  def optionToResponse[T](option: Option[T])(implicit writes: Writes[T]): Response
}

/**
 *  An implementation of the HttpResponseBuilder trait.
 */
class HttpResponseBuilderImpl extends HttpResponseBuilder {

  /**
   *  Converts an Option of T to a Response.
   */
  def optionToResponse[T](option: Option[T])(implicit writes: Writes[T]): Response = {
    option match {
      case Some(value) => Response.json(Json.toJson(value).toString)
      case None        => Response.status(Status.NotFound)
    }
  }
}

object HttpResponseBuilderImpl {

  /**
   *  A ZLayer that provides live implementation of HttpResponseBuilder.
   */
  val live: ULayer[HttpResponseBuilder] = ZLayer.succeed(new HttpResponseBuilderImpl)
}
