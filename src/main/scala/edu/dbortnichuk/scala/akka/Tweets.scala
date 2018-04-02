package edu.dbortnichuk.scala.akka

import java.nio.file.Paths

import akka.stream._
import akka.stream.scaladsl._
import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.util.ByteString
import java.time.Instant
import scala.concurrent.duration._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

final case class Author(handle: String)

final case class Hashtag(name: String)

final case class Tweet(author: Author, timestamp: Long, body: String) {
  def hashtags: Set[Hashtag] =
    body.split(" ").collect { case t if t.startsWith("#") => Hashtag(t) }.toSet
}

object Tweets extends App {

  implicit val system = ActorSystem("reactive-tweets")
  implicit val materializer = ActorMaterializer()

  val akkaTag = Hashtag("#akka")

  val tweetOrigin = Seq(
    Tweet(Author("Silvester"), Instant.now().toEpochMilli, "Adriannnneee!! #Rokky1 #Rokky2"),
    Tweet(Author("Arnold"), Instant.now().toEpochMilli, "I'll be baack! #Terminator"),
    Tweet(Author("Bruce"), Instant.now().toEpochMilli, "Yippie Ki Yay MotherFucker #DieHard1 #DieHard2"),
    Tweet(Author("Mel"), Instant.now().toEpochMilli, "Freeeeedoooom!! #BraveHeart"),
    Tweet(Author("Patrik"), Instant.now().toEpochMilli, "Akka is cool #akka"),
  )

  val tweets: Source[Tweet, NotUsed] = Source.fromIterator(() => tweetOrigin.iterator)

  val authors: Source[Author, NotUsed] =
    tweets
      .filter(_.hashtags.contains(akkaTag))
      .map(_.author)

  //authors.runWith(Sink.foreach(println))

  val hashtags: Source[Hashtag, NotUsed] = tweets.mapConcat(_.hashtags.toList)

  //hashtags.runWith(Sink.foreach(println))

  def lineSink(filename: String): Sink[String, Future[IOResult]] =
    Flow[String]
      .map(s => ByteString(s + "\n"))
      .toMat(FileIO.toPath(Paths.get(filename)))(Keep.right)

  def fileSink[A](fileName: String): Sink[A, Future[IOResult]] = {
    Flow[A]
      .map(s => ByteString(s.toString + "\n"))
      .toMat(FileIO.toPath(Paths.get(fileName)))(Keep.right)
  }

  val writeAuthors: Sink[Author, Future[IOResult]] = fileSink[Author]("authors.txt")
  val writeHashtags: Sink[Hashtag, Future[IOResult]] = fileSink[Hashtag]("hashtags.txt")

  val g = RunnableGraph.fromGraph(GraphDSL.create() { implicit b =>
    import GraphDSL.Implicits._

    val bcast = b.add(Broadcast[Tweet](2))
    tweets ~> bcast.in
    bcast.out(0) ~> Flow[Tweet].map(_.author) ~> writeAuthors
    bcast.out(1) ~> Flow[Tweet].mapConcat(_.hashtags.toList) ~> writeHashtags
    ClosedShape
  })
  //g.run

  //  tweets
  //    //.buffer(1, OverflowStrategy.dropHead)
  //    .throttle(1, 1.second, 1, ThrottleMode.shaping)//serves as long running computation
  //    //.map(identity)
  //    .runWith(Sink.foreach(println))

  val count: Flow[Tweet, Int, NotUsed] = Flow[Tweet].map(_ => 1)
  val sumSink: Sink[Int, Future[Int]] = Sink.fold[Int, Int](0)(_ + _)
  val counterGraph: RunnableGraph[Future[Int]] =
    tweets
      .via(count)
      .toMat(sumSink)(Keep.right)
  val sum: Future[Int] = counterGraph.run()
  sum.foreach {
    c =>
      println(s"Total tweets processed: $c")
      system.terminate()
  }

}
