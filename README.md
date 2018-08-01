# Cinnamon Akka Streams Demo

Demo showing work-in-progress for Akka Streams instrumentation, including [Akka Stream metrics], [Scala Future metrics], and [Akka Stream tracing].

> **Note**: Telemetry for Akka Streams is [now released] and available in Cinnamon 2.10.

[Akka Stream metrics]: https://developer.lightbend.com/docs/cinnamon/current/instrumentations/akka-streams/akka-streams.html
[Scala Future metrics]: https://developer.lightbend.com/docs/cinnamon/current/instrumentations/scala/scala-futures.html
[Akka Stream tracing]: https://developer.lightbend.com/docs/cinnamon/current/extensions/opentracing.html
[now released]: https://developer.lightbend.com/blog/2018-07-31-cinnamon-2-10-with-akka-stream-telemetry/


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

Download and start the [Cinnamon Prometheus Docker sandbox] (with Prometheus and Grafana).

Start up [Zipkin in Docker]:
 
```
docker run -d --name zipkin -p 9411:9411 openzipkin/zipkin
```

Run the demo application:

```
sbt run
```

[Cinnamon Prometheus Docker sandbox]: https://developer.lightbend.com/docs/cinnamon/current/plugins/prometheus/prometheus-sandbox.html
[Zipkin in Docker]: https://zipkin.io/pages/quickstart.html

### Metrics

Metrics will be available in Grafana:

[http://localhost:3000](http://localhost:3000)

Sign in with `admin` / `admin`. Enable the Cinnamon Prometheus app. View dashboards for Akka Streams and Scala Futures.

### Traces

Traces will be available in Zipkin:

[http://localhost:9411](http://localhost:9411)
