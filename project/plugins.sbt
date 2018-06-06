addSbtPlugin("com.lightbend.cinnamon" % "sbt-cinnamon" % "2.10.0-20180605-26e7708-streams")

credentials += Credentials(Path.userHome / ".lightbend" / "commercial.credentials")

resolvers += Resolver.url("lightbend-commercial",
  url("https://repo.lightbend.com/commercial-releases"))(Resolver.ivyStylePatterns)
