package com.github.salamonpavel.zio.model

import com.github.salamonpavel.zio.model.ApiResponseStatus.ApiResponseStatus
import play.api.libs.json.{Json, Reads, Writes}

/**
 *  A trait that represents an API response.
 */
trait SuccessApiResponse {

  /**
   *  The status of the API response.
   */
  def status: ApiResponseStatus
}

/**
 *  A trait that represents a successful API response containing single data item.
 */
case class SingleSuccessApiResponse[T](status: ApiResponseStatus, data: T) extends SuccessApiResponse

object SingleSuccessApiResponse {

  /**
   *  A JSON encoder/decoder for the SingleApiResponse class.
   */
  implicit def reads[T: Reads]: Reads[SingleSuccessApiResponse[T]] = Json.reads[SingleSuccessApiResponse[T]]
  implicit def writes[T: Writes]: Writes[SingleSuccessApiResponse[T]] = Json.writes[SingleSuccessApiResponse[T]]
}

/**
 *  A trait that represents a successful API response containing multiple data items.
 */
case class MultiSuccessApiResponse[T](status: ApiResponseStatus, data: Seq[T]) extends SuccessApiResponse

object MultiSuccessApiResponse {

  /**
   *  A JSON encoder/decoder for the MultiApiResponse class.
   */
  implicit def writes[T: Writes]: Writes[MultiSuccessApiResponse[T]] = Json.writes[MultiSuccessApiResponse[T]]
  implicit def reads[T: Reads]: Reads[MultiSuccessApiResponse[T]] = Json.reads[MultiSuccessApiResponse[T]]
}
