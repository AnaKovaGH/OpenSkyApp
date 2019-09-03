package sample

import akka.actor.Actor

class TransformingActor extends Actor {
  override def receive: Receive = {
    case _ => println("Transform")
  }
}