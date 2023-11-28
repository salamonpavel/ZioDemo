package com.github.salamonpavel.zio.util

import zio._
import zio.http.Response
import zio.http.model.Status
import zio.json.{EncoderOps, JsonEncoder}

trait HttpResponseBuilder {

  /**
   *  Converts an Option of T to a Response.
   *
   *  @param option The Option of T.
   *  @return A Response.
   */
  def optionToResponse[T](option: Option[T])(implicit encoder: JsonEncoder[T]): Response
}

class HttpResponseBuilderImpl extends HttpResponseBuilder {

  /**
   *  Converts an Option of T to a Response.
   *
   *  @param option The Option of T.
   *  @return A Response.
   */
  def optionToResponse[T](option: Option[T])(implicit encoder: JsonEncoder[T]): Response = {
    option match {
      case Some(value) => Response.json(value.toJson)
      case None => Response.status(Status.NotFound)
    }
  }
}

object HttpResponseBuilderImpl {

  /**
   *  A ZLayer that provides live implementation of HttpResponseBuilder.
   */
  val live: ULayer[HttpResponseBuilder] = ZLayer.succeed(new HttpResponseBuilderImpl)
}
