addSbtPlugin("com.lightbend.cinnamon" % "sbt-cinnamon" % "2.9.0-20180219-885e3ae-streams")

credentials += Credentials(Path.userHome / ".lightbend" / "commercial.credentials")

resolvers += Resolver.url("lightbend-commercial",
  url("https://repo.lightbend.com/commercial-releases"))(Resolver.ivyStylePatterns)
