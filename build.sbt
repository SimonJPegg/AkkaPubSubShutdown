name := "akka_reqres_bus"

version := "0.1"

scalaVersion := "2.11.11"

lazy val akkaVersion = "2.5.6"

lazy val akkaActor = "com.typesafe.akka" %% "akka-actor" % akkaVersion
lazy val akkaSlf4j = "com.typesafe.akka" %% "akka-slf4j" % akkaVersion
lazy val logback = "ch.qos.logback" % "logback-classic" % "1.0.9"
lazy val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2"


libraryDependencies ++=Seq(akkaActor,akkaSlf4j,logback,scalaLogging)