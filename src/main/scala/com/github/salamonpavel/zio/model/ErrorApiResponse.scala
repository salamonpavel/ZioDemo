package com.github.salamonpavel.zio.model

import com.github.salamonpavel.zio.model.ApiResponseStatus.ApiResponseStatus
import play.api.libs.json.{Json, Reads, Writes}

/**
 *  A trait that represents an unsuccessful API response containing an error message.
 */
case class ErrorApiResponse(status: ApiResponseStatus, message: String)

object ErrorApiResponse {

  /**
   *  A JSON encoder/decoder for the ErrorApiResponse class.
   */
  implicit val reads: Reads[ErrorApiResponse] = Json.reads[ErrorApiResponse]
  implicit val write: Writes[ErrorApiResponse] = Json.writes[ErrorApiResponse]
}
