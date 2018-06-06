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

  def delay(i: Int): Int = {
    if (probability(0.1)) Thread.sleep(increase(i)) else Thread.sleep(i); i
  }

  def random(min: Int, max: Int): Int = {
    val mean = (max + min) / 2.0
    val stddev = (max - min) / 8.0
    math.round(math.min(math.max(ThreadLocalRandom.current.nextGaussian * stddev + mean, min), max)).toInt
  }

  def increase(i: Int): Int = i * random(min = 1, max = 10)

  def probability(p: Double): Boolean = ThreadLocalRandom.current.nextDouble < p

  def flaky(i: Int): Int = if (probability(0.1)) throw new RuntimeException("flaky") else i

  // stage functions

  val generate = () => Iterator.continually(random(min = 1, max = 10))

  val generateWithDelay = () => Iterator.continually(delay(random(min = 1, max = 10)))

  val one = (i: Int) => delay(i)

  val two = (i: Int) => probability(0.3)

  val three = (i: Int) => delay(increase(i))

  // instrumented streams

  // method to select in configuration
  def stream() = {
    Source.fromIterator(generate)
      .map(one)
      .map(three)
      .to(Sink.ignore)
      .run()
  }

  // multiple instances of the stream
  stream()
  stream()

  // method to select in configuration
  def first() = {
    Source.fromIterator(generate)
      .map(one).named("one")
      .via(Flow[Int].filter(two).named("two"))
      .via(Flow[Int].map(three).named("three"))
      .to(Sink.ignore)
      .named("first")
      .run()
  }

  // multiple instances of the "first" stream
  first()
  first()

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

  Source.fromIterator(() => Iterator.continually(random(min = 1, max = 5))).flatMapConcat { n =>
    Source(List.fill(n)(random(min = 1, max = 20))).mapAsync(n) { i =>
      Source.single(i)
        .map(i => flaky(delay(increase(i))))
        .recover { case _: Exception => 0 }
        .toMat(Sink.head)(Keep.right)
        .instrumented(name = "fourth")
        .run()
    }
  }.runWith(Sink.ignore)

  Source.fromIterator(() => Iterator.continually(random(min = 10, max = 50))).flatMapConcat { n =>
    Source(List.fill(n)(random(min = 50, max = 100))).mapAsync(n) { i =>
      implicit val executionContext = system.dispatchers.lookup("small-dispatcher")
      FutureNamed("fifth.one")(one(i))
        .mapNamed("fifth.three")(three)
    }
  }.runWith(Sink.ignore)

  RunnableGraph.fromGraph(GraphDSL.create() { implicit b =>
    import GraphDSL.Implicits._
    val source = Source.fromIterator(generateWithDelay)
      .mapAsyncUnordered(4)(i => FutureNamed("sixth.one")(one(i)))
    val balance = b.add(Balance[Int](2))
    source ~> balance.in
    balance.out(0) ~> Flow[Int].filter(two).mapAsyncUnordered(4)(i => FutureNamed("sixth.three.left")(three(increase(i)))).to(Sink.ignore)
    balance.out(1) ~> Flow[Int].mapAsyncUnordered(4)(i => FutureNamed("sixth.three.right")(three(i))).to(Sink.ignore)
    ClosedShape
  }).instrumented(name = "sixth").run()

  println("Press enter to exit ...")
  System.in.read()
  println("Stopping ...")
  system.terminate()
}
