package com.github.salamonpavel.zio.model

import zio.json.{DeriveJsonEncoder, JsonEncoder}

/**
 *  A case class representing an actor.
 *
 *  @param actorId The ID of the actor.
 *  @param firstName The first name of the actor.
 *  @param lastName The last name of the actor.
 */
case class Actor(actorId: Int, firstName: String, lastName: String)

object Actor {

  /**
   *  An implicit JsonEncoder for the Actor case class.
   *  This encoder is used to convert instances of Actor to JSON.
   */
  implicit val encoder: JsonEncoder[Actor] = DeriveJsonEncoder.gen[Actor]
}
