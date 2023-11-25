package com.github.salamonpavel.zio.exception

/**
 *  An object representing application-specific errors.
 */
sealed trait AppError extends Throwable

/**
 *  An error indicating that a required parameter is missing.
 */
case class RequiredParameterMissingError(message: String) extends AppError

/**
 *  An error indicating that a parameter is not in the expected format.
 */
case class ParameterNumberFormatError(message: String) extends AppError

/**
 *  An error indicating that a database operation failed.
 */
case class DatabaseError(message: String) extends AppError