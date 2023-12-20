ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"

lazy val root = (project in file("."))
  .settings(
    name := "ZioDemo"
  )

scalacOptions += "-Ymacro-annotations"

lazy val zioVersion = "2.0.19"
lazy val zioConfigVersion = "4.0.0-RC16"
lazy val faDbVersion = "0.2.0+48-2799189f-SNAPSHOT"

resolvers += Resolver.mavenLocal

libraryDependencies ++= Seq(
  // cats & cats effect
  "org.typelevel" %% "cats-core" % "2.10.0",
  "org.typelevel" %% "cats-effect" % "3.5.2",

  // zio
  "dev.zio" %% "zio" % zioVersion,
  "dev.zio" %% "zio-macros" % zioVersion,
  // zio logging
  "dev.zio" %% "zio-logging" % "2.1.15",
  // zio config typesafe
  "dev.zio" %% "zio-config" % zioConfigVersion,
  "dev.zio" %% "zio-config-magnolia" % zioConfigVersion,
  "dev.zio" %% "zio-config-typesafe" % zioConfigVersion,

  // tapir with http4s
  "com.softwaremill.sttp.tapir" %% "tapir-http4s-server-zio" % "1.9.4",
  // http4s backend
  "org.http4s" %% "http4s-blaze-server" % "0.23.15",
  // swagger
  "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % "1.9.5",

  // play json
  "com.typesafe.play" %% "play-json" % "2.9.4",
  "com.softwaremill.sttp.tapir" %% "tapir-json-play" % "1.9.5",

  // fa-db & slick & pg dependencies
  "za.co.absa.fa-db" %% "core" % faDbVersion,
//  "za.co.absa.fa-db" %% "slick" % faDbVersion,
  "za.co.absa.fa-db" %% "doobie" % faDbVersion,
//  "com.github.tminglei" %% "slick-pg" % "0.20.4", // has to be version 0.20.4

  // zio test and mockito
  "dev.zio" %% "zio-test" % zioVersion % Test,
  "dev.zio" %% "zio-test-sbt" % zioVersion % Test,
  "dev.zio" %% "zio-test-magnolia" % zioVersion % Test,
  "dev.zio" %% "zio-test-junit" % zioVersion % Test,
  "com.github.sbt" % "junit-interface" % "0.13.3" % Test,
  "org.mockito" % "mockito-core" % "3.12.4" % Test
)

testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")