package com.github.salamonpavel.zio.repository

import org.junit.runner.RunWith
import zio.Scope
import zio.test.{Spec, TestEnvironment, ZIOSpecDefault}
import zio.test.junit.ZTestJUnitRunner

@RunWith(classOf[ZTestJUnitRunner])
class ActorsRepositorySpec extends ZIOSpecDefault {
  override def spec: Spec[TestEnvironment with Scope, Any] = ???
}
