package com.github.salamonpavel.zio.model

import play.api.libs.json.{Json, Reads}

/**
 *  A class representing a request to create an actor.
 *
 *  @param firstName The first name of the actor.
 *  @param lastName The last name of the actor.
 */
case class CreateActorRequestBody(firstName: String, lastName: String)

object CreateActorRequestBody {

  /**
   *  A JSON reader for the CreateActorRequestBody class.
   */
  implicit val reads: Reads[CreateActorRequestBody] = Json.reads[CreateActorRequestBody]
}
