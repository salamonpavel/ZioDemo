ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.12.18"

lazy val root = (project in file("."))
  .settings(
    name := "ZioDemo"
  )

libraryDependencies ++= Seq(
  // zio
  "dev.zio" %% "zio" % "2.0.19",
  // http
  "dev.zio" %% "zio-http" % "0.0.5",
  // json
  "dev.zio" %% "zio-json" % "0.6.2",
  // Fa-db & Slick & PG dependencies
  "za.co.absa.fa-db" %% "core" % "0.2.0",
  "za.co.absa.fa-db" %% "slick" % "0.2.0",
  "com.github.tminglei" %% "slick-pg" % "0.20.4"
)