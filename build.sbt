import sbt._
import Keys._

name := "Groosker Payment API"

version := "1.1.5-SNAPSHOT"

organization := "com.groosker"

scalaVersion := "2.10.1"

resolvers ++= Seq (
        "erisRepo" at "http://88.198.24.198/maven/", 
        ScalaToolsSnapshots, ScalaToolsReleases, DefaultMavenRepository,
        "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
)



publishTo := Some(FileRepository("Groosker Repo", Resolver.defaultFileConfiguration, Patterns(true, Option(System.getProperty("publish.dir")).getOrElse(System.getProperty("user.dir")) + "/[organization]/[module](_[scalaVersion])/[revision]/[artifact](_[scalaVersion])-[revision](-[classifier]).[ext]")))

{
libraryDependencies ++= Seq (
        "net.databinder.dispatch" %% "dispatch-core" % "0.10.0",
        "org.specs2" %% "specs2" % "1.14" % "test",
        "junit" % "junit" % "4.7" % "test",
        "net.liftweb" %% "lift-json" % "2.5-RC6")
}
