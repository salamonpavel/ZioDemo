package com.github.salamonpavel.zio.service

import org.junit.runner.RunWith
import zio.{Ref, Scope}
import zio.test._
import zio.test.junit.ZTestJUnitRunner

@RunWith(classOf[ZTestJUnitRunner])
class ActorsServiceSpec extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment with Scope, Any] = suite("first suite") {
    test("first test") {
      assert(1)(Assertion.equalTo(1))
    }
    test("updating ref") {
      for {
        r <- Ref.make(0)
        _ <- r.update(_ + 1)
        v <- r.get
      } yield assertTrue(v == 1)
    }
  }

}

// Two flavours of assertions
// smart assertions - unified for both ordinary values and zio effects
// classic assertions = assert for ordinary values, assertZIO for zio effects