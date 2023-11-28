ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.12.18"

lazy val root = (project in file("."))
  .settings(
    name := "ZioDemo"
  )

lazy val zioVersion = "2.0.19"
lazy val zioConfigVersion = "4.0.0-RC16"
lazy val faDbVersion = "0.2.0"

// to be able to fetch balta from local maven repo
// you can publish balta to local maven repo by running `sbt publishM2`
resolvers += Resolver.mavenLocal

libraryDependencies ++= Seq(
  // zio
  "dev.zio" %% "zio" % zioVersion,
  // zio http
  "dev.zio" %% "zio-http" % "0.0.5",
  // zio logging
  "dev.zio" %% "zio-logging" % "2.1.15",
  // zio json
  "dev.zio" %% "zio-json" % "0.6.2",
  // zio config typesafe
  "dev.zio" %% "zio-config" % zioConfigVersion,
  "dev.zio" %% "zio-config-typesafe" % zioConfigVersion,
  // fa-db & slick & pg dependencies
  "za.co.absa.fa-db" %% "core" % faDbVersion,
  "za.co.absa.fa-db" %% "slick" % faDbVersion,
  "com.github.tminglei" %% "slick-pg" % "0.20.4", // has to be version 0.20.4
  // zio test
  "dev.zio" %% "zio-test" % zioVersion % Test,
  "dev.zio" %% "zio-test-sbt" % zioVersion % Test,
  "dev.zio" %% "zio-test-magnolia" % zioVersion % Test,
  "dev.zio" %% "zio-test-junit" % zioVersion % Test,
  "com.github.sbt" % "junit-interface" % "0.13.3" % Test,
  // scalatest
  "org.scalatest" %% "scalatest" % "3.2.15" % Test,
  // balta https://github.com/AbsaOSS/balta
  "za.co.absa.balta" %% "balta" % "0.1.0-SNAPSHOT" % Test
)

testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")