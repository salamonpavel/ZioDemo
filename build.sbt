ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.12.18"

lazy val root = (project in file("."))
  .settings(
    name := "ZioDemo"
  )

libraryDependencies ++= Seq(
  // zio
  "dev.zio" %% "zio" % "2.0.19",
  // zio http
  "dev.zio" %% "zio-http" % "0.0.5",
  // zio logging
  "dev.zio" %% "zio-logging" % "2.1.15",
  // zio json
  "dev.zio" %% "zio-json" % "0.6.2",
  // zio config typesafe
  "dev.zio" %% "zio-config" % "4.0.0-RC16",
  "dev.zio" %% "zio-config-typesafe" % "4.0.0-RC16",
  // fa-db & slick & pg dependencies
  "za.co.absa.fa-db" %% "core" % "0.2.0",
  "za.co.absa.fa-db" %% "slick" % "0.2.0",
  "com.github.tminglei" %% "slick-pg" % "0.20.4", // has to be version 0.20.4
  "org.scalatest" %% "scalatest" % "3.2.15" % Test
)