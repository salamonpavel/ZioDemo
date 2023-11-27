package com.github.salamonpavel.zio.database

import com.github.salamonpavel.zio.model.Actor
import za.co.absa.balta.DBTestSuite

class GetActorByIdTest extends DBTestSuite {

  test("Get actor by id - actor with given id exists EXISTING") {
    val expectedActor = Actor(actorId = 1, firstName = "Malin", lastName = "Akerman")

    function("runs.get_actor_by_id")
      .setParam("i_actor_id", 1)
      .execute { queryResult =>
        assert(queryResult.hasNext)
        val row = queryResult.next()
        assert(row.getInt("actor_id").get == expectedActor.actorId)
        assert(row.getString("first_name").get == expectedActor.firstName)
        assert(row.getString("last_name").get == expectedActor.lastName)
        assert(!queryResult.hasNext)
      }
  }

  import za.co.absa.balta.classes.DefaultColumnNameMappers.SnakeCaseForCamelCaseMapper

  test("Get actor by id - actor with given id exists PROPOSAL") {
    val expectedActor = Actor(actorId = 1, firstName = "Malin", lastName = "Akerman")

    function("runs.get_actor_by_id")
      .setParam("i_actor_id", 1)
      .execute { queryResult =>
        assert(queryResult.hasNext)
        val row = queryResult.next()
        val actualActor = row.toProductType[Actor]
        assert(expectedActor == actualActor)
        assert(!queryResult.hasNext)
      }
  }

}