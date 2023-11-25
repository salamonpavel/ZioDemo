package com.github.salamonpavel.zio.exception

sealed trait AppError extends Throwable

case class RequiredParameterMissingError(message: String) extends AppError
case class ParameterNumberFormatError(message: String) extends AppError
