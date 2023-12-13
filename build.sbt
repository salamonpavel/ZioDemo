ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"

lazy val root = (project in file("."))
  .settings(
    name := "ZioDemo"
  )

lazy val zioVersion = "2.0.19"
lazy val zioConfigVersion = "4.0.0-RC16"
lazy val faDbVersion = "0.2.0+48-2799189f-SNAPSHOT" // 0.2.0

// to be able to fetch balta from local maven repo
// you can publish balta to local maven repo by running `sbt publishM2`
resolvers += Resolver.mavenLocal

// lazy val macros = Seq(
//   libraryDependencies ++= {
//     CrossVersion.partialVersion(scalaVersion.value) match {
//       case Some((2, 11 | 12)) => List(compilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.patch))
//       case _                  => List()
//     }
//   },
//   scalacOptions ++= {
//     CrossVersion.partialVersion(scalaVersion.value) match {
//       case Some((2, y)) if y == 11 => Seq("-Xexperimental")
//       case Some((2, y)) if y == 13 => Seq("-Ymacro-annotations")
//       case _                       => Seq.empty[String]
//     }
//   },
//   // remove false alarms about unused implicit definitions in macros
//   scalacOptions ++= {
//     CrossVersion.partialVersion(scalaVersion.value) match {
//       case Some((2, _)) => Seq("-Ywarn-macros:after")
//       case _            => Seq.empty[String]
//     }
//   }
// )

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % "2.10.0",
  "org.typelevel" %% "cats-effect" % "3.5.2",

// zio
  "dev.zio" %% "zio" % zioVersion,
  // zio http
  "dev.zio" %% "zio-http" % "0.0.5",
  // tapir with http4s
  "com.softwaremill.sttp.tapir" %% "tapir-http4s-server-zio" % "1.9.4",
//  "com.softwaremill.sttp.tapir" %% "tapir-zio-http-server" % "1.9.5",
  // http4s backend
  "org.http4s" %% "http4s-blaze-server" % "0.23.15",
  // swagger
  "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % "1.9.5",
  // zio logging
  "dev.zio" %% "zio-logging" % "2.1.15",
  // play json
  "com.typesafe.play" %% "play-json" % "2.9.4",
  "com.softwaremill.sttp.tapir" %% "tapir-json-play" % "1.9.5",


  // zio config typesafe
  "dev.zio" %% "zio-config" % zioConfigVersion,
  "dev.zio" %% "zio-config-typesafe" % zioConfigVersion,
  // fa-db & slick & pg dependencies
  "za.co.absa.fa-db" %% "core" % faDbVersion,
  "za.co.absa.fa-db" %% "slick" % faDbVersion,
  "com.github.tminglei" %% "slick-pg" % "0.20.4", // has to be version 0.20.4
  // zio test
  "dev.zio" %% "zio-test" % zioVersion % Test,
//  "dev.zio" %% "zio-test-sbt" % zioVersion % Test,
//  "dev.zio" %% "zio-test-magnolia" % zioVersion % Test,
  "dev.zio" %% "zio-test-junit" % zioVersion % Test,
  "com.github.sbt" % "junit-interface" % "0.13.3" % Test,
  // scalatest
  "org.scalatest" %% "scalatest" % "3.2.15" % Test,
)

testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")