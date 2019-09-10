package actors

import akka.actor.{Actor, ActorRef, Props}
import messages.{IngestDataMessage, StartMessage}


class SupervisorActor extends Actor {
  val sendingKafkaActor: ActorRef = context.actorOf(Props[SendingKafkaActor])
  val calculatingActor: ActorRef = context.actorOf(Props(new CalculatingActor(sendingKafkaActor)), name = "calculatingActor")
  val transformingActor: ActorRef = context.actorOf(Props(new TransformingActor(calculatingActor)), name = "transformingActor")
  val ingestingActor: ActorRef = context.actorOf(Props(new IngestingActor(transformingActor)), name = "ingestingActor")

  override def receive: Receive = {
    case StartMessage => ingestingActor ! IngestDataMessage
    case _ => println("Unknown message. Supervisor.")
  }
}