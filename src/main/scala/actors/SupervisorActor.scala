package actors


import akka.actor.{Actor, ActorRef, Props}

import messages.{CompleteWork, IngestDataMessage, StartMessage}


class SupervisorActor extends Actor {
  val sendingKafkaActor: ActorRef = context.actorOf(Props[SendingKafkaActor], name = "sendingKafkaActor")
  val calculatingActor: ActorRef = context.actorOf(Props[CalculatingActor], name = "calculatingActor")
  val transformingActor: ActorRef = context.actorOf(Props[TransformingActor], name = "transformingActor")
  val ingestingActor: ActorRef = context.actorOf(Props[IngestingActor], name = "ingestingActor")
  val serverAPIActor: ActorRef = context.actorOf(Props[ServerAPIActor], name = "serverAPIActor")

  override def receive: Receive = {
    case StartMessage => ingestingActor ! IngestDataMessage
    case CompleteWork =>
      println("Work is completed")
      ingestingActor ! IngestDataMessage
    case _ => println("Unknown message. Supervisor.")
  }
}
