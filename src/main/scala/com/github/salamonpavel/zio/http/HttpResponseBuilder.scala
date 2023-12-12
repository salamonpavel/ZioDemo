package com.github.salamonpavel.zio.http

import com.github.salamonpavel.zio.exception._
import com.github.salamonpavel.zio.model.{ApiResponseStatus, ErrorApiResponse, MultiApiResponse, SingleApiResponse}
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

  /**
   *  Converts a Seq of T to a Response.
   *
   *  @param seq The Seq of T.
   *  @return A Response.
   */
  def seqToResponse[T](seq: Seq[T])(implicit writes: Writes[T]): Response

  /**
   *  Converts a T to a Response for POST requests.
   *
   *  @param value The T.
   *  @return A Response.
   */
  def successPostResponse[T](value: T)(implicit writes: Writes[T]): Response

  /**
   *  Converts an AppError to a Response.
   *  @param error The AppError.
   *  @return A Response.
   */
  def appErrorToResponse(error: AppError): Response
}

/**
 *  An implementation of the HttpResponseBuilder trait.
 */
class HttpResponseBuilderImpl extends HttpResponseBuilder {

  /**
   *  Converts an Option of T to a Response.
   */
  def optionToResponse[T](option: Option[T])(implicit writes: Writes[T]): Response = option match {
    case Some(value) =>
      val apiResponse = SingleApiResponse(ApiResponseStatus.Success, value)
      Response.json(Json.toJson(apiResponse).toString)
    case None =>
      val errorResponse = ErrorApiResponse(ApiResponseStatus.NotFound, "The requested resource could not be found.")
      Response.json(Json.toJson(errorResponse).toString).setStatus(Status.NotFound)
  }

  /**
   *  Converts a Seq of T to a Response.
   */
  def seqToResponse[T](seq: Seq[T])(implicit writes: Writes[T]): Response = {
    val apiResponse = MultiApiResponse(ApiResponseStatus.Success, seq)
    Response.json(Json.toJson(apiResponse).toString)
  }

  /**
   *  Converts a T to a Response for POST requests.
   */
  def successPostResponse[T](value: T)(implicit writes: Writes[T]): Response = {
    val apiResponse = SingleApiResponse(ApiResponseStatus.Created, value)
    Response.json(Json.toJson(apiResponse).toString).setStatus(Status.Created)
  }

  /**
   *  Converts an AppError to a Response.
   */
  def appErrorToResponse(error: AppError): Response = {
    val (status, apiResponseStatus) = error match {
      case _: ParameterMissingError => (Status.BadRequest, ApiResponseStatus.BadRequest)
      case _: ParameterFormatError  => (Status.BadRequest, ApiResponseStatus.BadRequest)
      case _: RequestBodyError      => (Status.BadRequest, ApiResponseStatus.BadRequest)
      case _: ServiceError          => (Status.InternalServerError, ApiResponseStatus.Error)
      case _                        => (Status.BadRequest, ApiResponseStatus.Error)
    }

    val errorApiResponse = ErrorApiResponse(apiResponseStatus, error.message)
    Response.json(Json.toJson(errorApiResponse).toString).setStatus(status)
  }
}

object HttpResponseBuilderImpl {

  /**
   *  A ZLayer that provides live implementation of HttpResponseBuilder.
   */
  val layer: ULayer[HttpResponseBuilder] = ZLayer.succeed(new HttpResponseBuilderImpl)
}
