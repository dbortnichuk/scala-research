package edu.dbortnichuk.scala.akka

import akka.actor.{Actor, ActorSystem, Props}
import edu.dbortnichuk.scala.akka.PassengerCountActor.{Enter, Leave, NumberOfPassengers}
import edu.dbortnichuk.scala.akka.PassengerCountActor.NumberOfPassengers.Get


object AkkaTest extends App {

  val system = ActorSystem("ActorSystem")

  val userStorage = system.actorOf(Props[PassengerCountActor], "PassengerCountActor")

  userStorage ! Enter
  userStorage ! Enter
  //userStorage ! Leave
  userStorage ! Get

  Thread.sleep(1000)
  system.terminate()


}

class PassengerCountActor extends Actor {
  override def receive: Receive = handleMessage(0)

  def handleMessage(numberOfPassengers: Int): Receive = {
    case Enter => context.become(handleMessage(numberOfPassengers + 1))
    case Leave if numberOfPassengers > 0 => context.become(handleMessage(numberOfPassengers - 1))
    case NumberOfPassengers.Get => {
      println(numberOfPassengers)
      sender ! NumberOfPassengers.Result(numberOfPassengers)
    }
  }
}

object PassengerCountActor {
  def props: Props = Props(new PassengerCountActor)

  case object Enter

  case object Leave

  object NumberOfPassengers {
    case object Get
    case class Result(numberOfPassengers: Int)
  }

}
