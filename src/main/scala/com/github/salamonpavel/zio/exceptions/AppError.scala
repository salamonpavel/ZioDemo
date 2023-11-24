package com.github.salamonpavel.zio.exceptions

sealed trait AppError extends Throwable

case class ParseError(message: String) extends AppError
