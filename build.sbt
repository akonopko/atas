import Dependencies._

enablePlugins(JavaAppPackaging)

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.atlas",
      scalaVersion := "2.12.6",
      version := "0.1.0-SNAPSHOT"
    )),
    resolvers += Resolver.url("bintray-sbt-plugins", url("https://dl.bintray.com/sbt/sbt-plugin-releases/"))(Resolver.ivyStylePatterns),
    resolvers += Resolver.sbtPluginRepo("releases"),
    resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
    name := "atlas",
    libraryDependencies ++= {
      val akkaHttpVersion = "10.1.3"
      val configVersion = "1.3.1"
      val circeVersion = "0.9.3"
      val akkaVersion = "2.5.23"
      Seq(
//        "org.slf4j" % "slf4j-api" % "1.7.28",
//        "org.slf4j" % "slf4j-simple" % "1.7.28",
        "io.spray" %%  "spray-json" % "1.3.4",
        "com.typesafe" % "config" % configVersion,
        "com.typesafe.akka" %% "akka-actor" % akkaVersion,
        "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
        "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
        "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion,
        "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion,
        "com.typesafe.slick" %% "slick" % "3.3.0",
        "com.typesafe.slick" %% "slick-hikaricp" % "3.3.0",
        "org.postgresql" % "postgresql" % "42.1.2",
        "ch.qos.logback" % "logback-classic" % "1.2.3",
        "joda-time" % "joda-time" % "2.10.4",

        scalaTest % Test
      )
    }
  )
