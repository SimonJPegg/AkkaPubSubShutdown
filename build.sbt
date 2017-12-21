name := "akka_reqres_bus"

version := "0.1"

scalaVersion := "2.11.11"

lazy val akkaVersion = "2.5.6"
lazy val slf4jVersion = "1.7.25"

lazy val akkaActor = "com.typesafe.akka" %% "akka-actor" % akkaVersion
lazy val akkaSlf4j = "com.typesafe.akka" %% "akka-slf4j" % akkaVersion
lazy val slf4jAPI = "org.slf4j" % "slf4j-api" % slf4jVersion
lazy val slf4jLog4j = "org.slf4j" % "slf4j-log4j12" % slf4jVersion
lazy val log4j = "log4j" % "log4j" % "1.2.17"


libraryDependencies ++=Seq(akkaActor,akkaSlf4j,slf4jAPI,slf4jLog4j,log4j)