package com.github.salamonpavel.zio.model

import com.github.salamonpavel.zio.model.ApiResponseStatus.ApiResponseStatus
import play.api.libs.json.{Json, Writes}

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
   *  A JSON encoder for the SingleApiResponse class.
   */
  implicit def writes[T: Writes]: Writes[SingleApiResponse[T]] = Json.writes[SingleApiResponse[T]]
}

/**
 *  A trait that represents a successful API response containing multiple data items.
 */
case class MultiApiResponse[T](status: ApiResponseStatus, data: Seq[T]) extends ApiResponse

object MultiApiResponse {

  /**
   *  A JSON encoder for the MultiApiResponse class.
   */
  implicit def writes[T: Writes]: Writes[MultiApiResponse[T]] = Json.writes[MultiApiResponse[T]]
}

/**
 *  A trait that represents an unsuccessful API response containing an error message.
 */
case class ErrorApiResponse(status: ApiResponseStatus, message: String) extends ApiResponse
