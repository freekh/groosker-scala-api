import sbt._
import Keys._

name := "Groosker Payment API"

version := "1.1-SNAPSHOT"

organization := "com.groosker"

scalaVersion := "2.9.1"

resolvers ++= Seq (
        "erisRepo" at "http://88.198.24.198/maven/", 
        ScalaToolsSnapshots, ScalaToolsReleases, DefaultMavenRepository,
        "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
)

publishTo := Some(FileRepository("Groosker Repo", Resolver.defaultFileConfiguration, Patterns(Option(System.getProperty("publish.dir")).getOrElse(System.getProperty("user.dir")) + "[organisation]/[module]/[revision]/[module]-[revision].[artifact]")))

{
libraryDependencies ++= Seq (
        "net.databinder" %% "dispatch-http" % "[0.7.7, )",     
        "net.databinder" %% "dispatch-mime" % "[0.7.7, )",
        "org.specs2" %% "specs2" % "1.9" % "test",     
        "se.scalablesolutions.akka" % "akka-actor" % "1.2", 
        "se.scalablesolutions.akka" % "akka-remote" % "1.2")
}
