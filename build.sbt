scalaVersion := "2.12.10"

val http4sVersion = "0.20.13"

libraryDependencies += "org.http4s" %% "http4s-blaze-client" % http4sVersion
libraryDependencies += "org.http4s" %% "http4s-core" % http4sVersion
libraryDependencies += "org.http4s" %% "http4s-client" % http4sVersion
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"