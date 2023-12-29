package com.github.salamonpavel.zio.exception

/**
 *  An object representing application-specific errors.
 */
sealed trait AppError extends Throwable {
  def message: String
}

/**
 *  An error indicating that a database operation failed.
 */
case class DatabaseError(message: String) extends AppError

/**
 *  An error indicating that a service operation failed.
 */
case class ServiceError(message: String) extends AppError
