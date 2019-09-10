package actors

import akka.actor.{Actor, ActorRef, Props}
import messages.{IngestDataMessage, StartMessage}


class SupervisorActor extends Actor {
  val ingestingActor: ActorRef = context.actorOf(Props[IngestingActor])
  val calculatingActor: ActorRef = context.actorOf(Props[CalculatingActor])
  val sendingKafkaActor: ActorRef = context.actorOf(Props[SendingKafkaActor])
  val transformingActor: ActorRef = context.actorOf(Props[TransformingActor])



  override def receive: Receive = {
    case StartMessage => ingestingActor ! IngestDataMessage
    case _ => println("Unknown message. Supervisor.")
  }
}