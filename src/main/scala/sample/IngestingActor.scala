package sample

import akka.actor.Actor

class IngestingActor extends Actor {
  override def receive: Receive = {
    case _ => println("Ingest")
  }
}
