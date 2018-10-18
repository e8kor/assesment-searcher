import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "searcher",
      scalaVersion := "2.12.7",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "searcher",
    test in assembly := {},
    mainClass in assembly := Some("searcher.Main"),
    assemblyJarName in assembly := "../../app.jar",
    libraryDependencies += scalaTest % Test
  )
