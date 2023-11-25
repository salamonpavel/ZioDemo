package com.github.salamonpavel.zio.exception

sealed trait AppError extends Throwable

case class ParseError(message: String) extends AppError
