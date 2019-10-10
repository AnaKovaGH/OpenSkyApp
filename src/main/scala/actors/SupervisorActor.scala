package actors


import akka.actor.{Actor, ActorRef, Props}

import messages.{CompleteWork, IngestDataFromDatasource, StartWork}


class SupervisorActor extends Actor {
  val kafkaProducerActor: ActorRef = context.actorOf(Props[KafkaProducerActor], name = "kafkaProducerActor")
  val calculatingActor: ActorRef = context.actorOf(Props[CalculatingActor], name = "calculatingActor")
  val transformingActor: ActorRef = context.actorOf(Props[TransformingActor], name = "transformingActor")
  val ingestingActor: ActorRef = context.actorOf(Props[IngestingActor], name = "ingestingActor")

  override def receive: Receive = {
    case StartWork => ingestingActor ! IngestDataFromDatasource
    case CompleteWork =>
      println("Work is completed")
      ingestingActor ! IngestDataFromDatasource
    case _ => println("Unknown message. Supervisor.")
  }
}
