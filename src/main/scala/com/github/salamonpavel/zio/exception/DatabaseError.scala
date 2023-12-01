package com.github.salamonpavel.zio.exception

/**
 *  An error indicating that a database operation failed.
 */
case class DatabaseError(message: String) extends Throwable
