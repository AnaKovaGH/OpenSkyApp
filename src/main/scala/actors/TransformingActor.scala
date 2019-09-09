package actors

import akka.actor.Actor
import messages.IngestedDataMessage


class TransformingActor extends Actor {
  override def receive: Receive = {
    case IngestedDataMessage => println("Done sending")
    case _ => println("Transform")
  }
}