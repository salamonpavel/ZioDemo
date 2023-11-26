package com.github.salamonpavel.zio.model

import zio.json.{DeriveJsonDecoder, JsonDecoder}

/**
 *  A class representing a request to create an actor.
 *
 *  @param firstName The first name of the actor.
 *  @param lastName The last name of the actor.
 */
case class CreateActorRequestBody(firstName: String, lastName: String)

object CreateActorRequestBody {
  /**
   *  An implicit JsonDecoder for the CreateActor case class.
   *  This decoder is used to convert JSON to instances of CreateActor.
   */
  implicit val decoder: JsonDecoder[CreateActorRequestBody] = DeriveJsonDecoder.gen[CreateActorRequestBody]
}
