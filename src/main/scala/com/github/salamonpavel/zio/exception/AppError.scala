package com.github.salamonpavel.zio.exception

/**
 *  An object representing application-specific errors.
 */
sealed trait AppError extends Throwable {
  def message: String
}

/**
 *  An error indicating that a required parameter is missing.
 */
case class ParameterMissingError(message: String) extends AppError

/**
 *  An error indicating that a parameter is not in the expected format.
 */
case class ParameterFormatError(message: String) extends AppError

/**
 *  An error indicating that a request body is not in the expected format.
 */
case class RequestBodyError(message: String) extends AppError

/**
 *  An error indicating that a database operation failed.
 */
case class DatabaseError(message: String) extends AppError
