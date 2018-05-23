addSbtPlugin("com.lightbend.cinnamon" % "sbt-cinnamon" % "2.10.0-20180523-17f3e6d")

credentials += Credentials(Path.userHome / ".lightbend" / "commercial.credentials")

resolvers += Resolver.url("lightbend-commercial",
  url("https://repo.lightbend.com/commercial-releases"))(Resolver.ivyStylePatterns)
