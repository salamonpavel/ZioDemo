package com.github.salamonpavel.zio.database

import com.github.salamonpavel.zio.model.Actor
import slick.jdbc.{GetResult, PositionedResult}

/**
 *  A trait representing a converter from a Slick PositionedResult to an Actor.
 *  The trait is to be mixed into a SlickFunction returning an Actor.
 */
trait ActorSlickConverter {

  protected def slickConverter: GetResult[Actor] = GetResult(r => Actor(r.<<, r.<<, r.<<))

}
