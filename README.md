# Cinnamon Akka Streams Demo

Demo showing work-in-progress for [Akka Stream metrics] and [Scala Future metrics].

[Akka Stream metrics]: https://downloads.lightbend.com/cinnamon/docs/2.10.0-20180605-26e7708-streams/instrumentations/akka-streams/akka-streams.html
[Scala Future metrics]: https://downloads.lightbend.com/cinnamon/docs/2.10.0-20180605-26e7708-streams/instrumentations/scala/scala-futures.html 


## Credentials file

First make sure you have credentials set up for Lightbend Reactive Platform. You can find your username and password under your [Lightbend account].

Create a `~/.lightbend/commercial.credentials` file with:

```
realm = Bintray
host = dl.bintray.com
user = <username>
password = <password>
```

[Lightbend account]: https://www.lightbend.com/product/lightbend-reactive-platform/credentials


## Running the sample

Start up the Prometheus docker sandbox, with Grafana dashboards:

```
cd sandbox
docker-compose up
```

> Note: the Prometheus sandbox is configured for Docker for Mac. Since Prometheus needs an address to scrape from, the Prometheus configuration will need to be updated for other platforms. 

Run the demo application:

```
sbt run
```

Metrics will be available in Grafana:

[http://localhost:3000](http://localhost:3000)

Sign in with `admin` / `admin`. Enable the Cinnamon Prometheus app. View dashboards for Akka Streams and Scala Futures.
