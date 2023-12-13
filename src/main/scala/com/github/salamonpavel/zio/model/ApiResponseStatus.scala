package com.github.salamonpavel.zio.model

import com.github.salamonpavel.zio.model
import play.api.libs.json.{Reads, Writes}

/**
 *  Enumeration for possible statuses in the context of a REST API implementing CRUD operations.
 */
object ApiResponseStatus extends Enumeration {
  type ApiResponseStatus = Value

  /** The operation was successful. This could be used for any successful GET, POST, PUT, or DELETE request. */
  val Success = Value("success")

  /** A new resource was successfully created. This would typically be used for a successful POST request. */
  val Created = Value("created")

  /** An existing resource was successfully updated. This would typically be used for a successful PUT or PATCH request. */
  val Updated = Value("updated")

  /** An existing resource was successfully deleted. This would typically be used for a successful DELETE request. */
  val Deleted = Value("deleted")

  /** The requested resource could not be found. This could be used for a GET, PUT, PATCH, or DELETE request for a non-existent resource. */
  val NotFound = Value("not_found")

  /** An error occurred while processing the request. This could be used for any request that results in an error. */
  val Error = Value("error")

  /** The request was malformed or invalid. This could be used for any request that cannot be processed due to client-side issues. */
  val BadRequest = Value("bad_request")

  /** The client is not authorized to perform the requested operation. This could be used for any request that requires authentication or authorization. */
  val Unauthorized = Value("unauthorized")

  /** The client is authenticated but does not have permission to perform the requested operation. This could be used for any request that requires specific permissions. */
  val Forbidden = Value("forbidden")

  /** The request could not be completed due to a conflict with the current state of the resource (e.g., trying to create a resource that already exists). This could be used for POST, PUT, or PATCH requests. */
  val Conflict = Value("conflict")

  /** The server encountered an unexpected condition that prevented it from fulfilling the request. This could be used for any request that fails due to server-side issues. */
  val InternalServerError = Value("internal_server_error")

  implicit val apiResponseStatusReads: Reads[model.ApiResponseStatus.Value] = Reads.enumNameReads(ApiResponseStatus)
  implicit val apiResponseStatusWrites: Writes[model.ApiResponseStatus.Value] = Writes.enumNameWrites[ApiResponseStatus.type]
}
