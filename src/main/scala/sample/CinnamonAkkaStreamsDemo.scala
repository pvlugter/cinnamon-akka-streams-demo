package sample

import akka.actor._
import akka.stream._
import akka.stream.scaladsl._
import java.util.concurrent.ThreadLocalRandom
import scala.concurrent.duration._

import com.lightbend.cinnamon.akka.stream.CinnamonAttributes.GraphWithInstrumented
import com.lightbend.cinnamon.scala.future.named._

object CinnamonAkkaStreamsDemo extends App {
  implicit val system = ActorSystem("sample")
  implicit val materializer = ActorMaterializer()
  import system.dispatcher

  // simulate work

  def delay(i: Int): Int = { Thread.sleep(i); i }

  def increase(i: Int): Int = i * random(10)

  def random(k: Int): Int = math.min(math.abs(ThreadLocalRandom.current.nextGaussian * k).toInt, 4 * k) + 1

  def probability(p: Double): Boolean = ThreadLocalRandom.current.nextDouble < p

  // stage functions

  val generate = () => Iterator.continually(random(10))

  val one = (i: Int) => delay(i)

  val two = (i: Int) => probability(0.3)

  val three = (i: Int) => delay(increase(i))

  // instrumented streams

  Source.fromIterator(generate)
    .map(one).named("one")
    .filter(two)
    .via(Flow[Int].map(three).named("three"))
    .to(Sink.ignore)
    .named("first")
    .instrumented(reportByName = true)
    .run()

  Source.fromIterator(generate)
    .mapAsync(4)(i => FutureNamed("second.one")(one(i)))
    .filter(two)
    .mapAsync(4)(i => FutureNamed("second.three")(three(i)))
    .to(Sink.ignore)
    .instrumented(name = "second")
    .run()

  Source.fromIterator(generate)
    .mapAsyncUnordered(4)(i => FutureNamed("third.one")(one(i)))
    .filter(two)
    .mapAsyncUnordered(4)(i => FutureNamed("third.three")(three(i)))
    .to(Sink.ignore)
    .instrumented(name = "third")
    .run()

  Source.fromIterator(generate)
    .groupBy(2, _ < 5)
    .map(one)
    .filter(two)
    .map(three)
    .mergeSubstreams
    .to(Sink.ignore)
    .instrumented(name = "fourth", reportByInstance = true)
    .run()

  println("Press enter to exit ...")
  System.in.read()
  println("Stopping ...")
  system.terminate()
}
