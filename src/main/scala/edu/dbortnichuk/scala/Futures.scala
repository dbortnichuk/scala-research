package edu.dbortnichuk.scala

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

object Futures extends App{

  val future1: Future[Int] = Future[Int] {
    throw new NumberFormatException("not 42!")
    //throw new IllegalArgumentException("not 42!")
    //42
  }

  val future2: Future[Int] = future1.recover {
    case exn: NumberFormatException =>
      43
  }

  val future3: Future[Int] = future1.recoverWith {
    case exn: NumberFormatException =>
      Future(44)
  }

  val future4: Future[String] = future1.transform(
    s = (result: Int) => (result * 10).toString,
    f = (exn: Throwable) => exn
  )

  val future5 = Future.failed[Int](new Exception("Oh noes!"))

  println(Await.result(future3, 1.second))

}
