lazy val cinnamonAkkaStreamsDemo = project
  .in(file("."))
  .enablePlugins(Cinnamon)
  .settings(
    scalaVersion := "2.12.4",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-stream" % "2.5.9",
      Cinnamon.library.cinnamonAkkaStream,
      Cinnamon.library.cinnamonPrometheusHttpServer
    ),
    cinnamon in run := true,
    connectInput in run := true
  )
