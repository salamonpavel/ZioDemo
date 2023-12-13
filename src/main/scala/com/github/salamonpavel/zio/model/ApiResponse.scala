package com.github.salamonpavel.zio.model

import com.github.salamonpavel.zio.model.ApiResponseStatus.ApiResponseStatus
import play.api.libs.json.{Format, Json, Reads, Writes}

/**
 *  A trait that represents an API response.
 */
trait ApiResponse {

  /**
   *  The status of the API response.
   */
  def status: ApiResponseStatus
}

/**
 *  A trait that represents a successful API response containing single data item.
 */
case class SingleApiResponse[T](status: ApiResponseStatus, data: T) extends ApiResponse

object SingleApiResponse {

  /**
   *  A JSON encoder/decoder for the SingleApiResponse class.
   */
  implicit def reads[T: Reads]: Reads[SingleApiResponse[T]] = Json.reads[SingleApiResponse[T]]
  implicit def writes[T: Writes]: Writes[SingleApiResponse[T]] = Json.writes[SingleApiResponse[T]]
}

/**
 *  A trait that represents a successful API response containing multiple data items.
 */
case class MultiApiResponse[T](status: ApiResponseStatus, data: Seq[T]) extends ApiResponse

object MultiApiResponse {

  /**
   *  A JSON encoder/decoder for the MultiApiResponse class.
   */
  implicit def writes[T: Writes]: Writes[MultiApiResponse[T]] = Json.writes[MultiApiResponse[T]]
  implicit def reads[T: Reads]: Reads[MultiApiResponse[T]] = Json.reads[MultiApiResponse[T]]
}

/**
 *  A trait that represents an unsuccessful API response containing an error message.
 */
case class ErrorApiResponse(status: ApiResponseStatus, message: String) extends ApiResponse

object ErrorApiResponse {

  /**
   *  A JSON encoder/decoder for the ErrorApiResponse class.
   */
  implicit val reads: Reads[ErrorApiResponse] = Json.reads[ErrorApiResponse]
  implicit val write: Writes[ErrorApiResponse] = Json.writes[ErrorApiResponse]
}
