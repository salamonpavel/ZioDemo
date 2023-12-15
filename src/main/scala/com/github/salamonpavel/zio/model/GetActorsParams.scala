package com.github.salamonpavel.zio.model

/**
 *  A case class representing query parameters for the GET /actors endpoint.
 *
 *  @param firstName The first name of the actor.
 *  @param lastName The last name of the actor.
 */
case class GetActorsParams(firstName: Option[String], lastName: Option[String])
