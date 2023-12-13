package com.github.salamonpavel.zio.model

import play.api.libs.json.{Json, Reads, Writes}

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
   *  A JSON encoder for the Actor class.
   */
  implicit val writes: Writes[Actor] = Json.writes[Actor]
  implicit val reads: Reads[Actor] = Json.reads[Actor]
}
