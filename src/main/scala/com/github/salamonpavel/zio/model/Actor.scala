package com.github.salamonpavel.zio.model

import zio.json.{DeriveJsonEncoder, JsonEncoder}

case class Actor(actorId: Int, firstName: String, lastName: String)

object Actor {
  implicit val encoder: JsonEncoder[Actor] = DeriveJsonEncoder.gen[Actor]
}
