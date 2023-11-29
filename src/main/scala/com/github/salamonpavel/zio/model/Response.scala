package com.github.salamonpavel.zio.model

import zio.json.{DeriveJsonEncoder, JsonEncoder}

/**
 *  A trait representing a response.
 */
sealed trait Response {
  def status: Status.Status
}

/**
 *  A trait representing a response with data.
 */
case class SuccessResponse[T](status: Status.Status, data: T) extends Response

object SuccessResponse {
  /**
   *  A JSON encoder for SuccessResponse.
   */
  implicit def encoder[T]: JsonEncoder[SuccessResponse[T]] = DeriveJsonEncoder.gen[SuccessResponse[T]]
}

case class ErrorResponse(status: Status.Status, message: String) extends Response

object ErrorResponse {
  /**
   *  A JSON encoder for ErrorResponse.
   */
  implicit val encoder: JsonEncoder[ErrorResponse] = DeriveJsonEncoder.gen[ErrorResponse]
}