package com.github.salamonpavel.zio.model

import play.api.libs.json.{Json, Reads, Writes}

/**
 *  A class representing a request to create an actor.
 *
 *  @param firstName The first name of the actor.
 *  @param lastName The last name of the actor.
 */
case class CreateActorRequestBody(firstName: String, lastName: String)

object CreateActorRequestBody {

  /**
   *  A JSON encoder/decoder for the CreateActorRequestBody class.
   */
  implicit val reads: Reads[CreateActorRequestBody] = Json.reads[CreateActorRequestBody]
  implicit val writes: Writes[CreateActorRequestBody] = Json.writes[CreateActorRequestBody]
}
