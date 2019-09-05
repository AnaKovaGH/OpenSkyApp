package actors

import akka.actor._

class CalculatingActor extends Actor {
  override def receive: Receive = {
    case _ => println("Calculate")
  }
}