package com.github.salamonpavel.zio.model

import zio.json.{DeriveJsonEncoder, JsonEncoder}

import java.sql.Date

case class Actor (actorId: Int, first_name: String, last_name: String, gender: String, dateOfBirth: Date)

object Actor {
  implicit val encoder: JsonEncoder[Actor] = DeriveJsonEncoder.gen[Actor]
}
