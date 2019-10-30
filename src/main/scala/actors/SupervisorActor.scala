package actors


import akka.actor.{Actor, ActorLogging, ActorRef, Props}

import messages.{CompleteWork, DataIngested, IngestDataFromDatasource, StartWork, UnknownMessage, WorkCompleted, WorkStarted}


class SupervisorActor extends Actor with ActorLogging {
  val kafkaProducerActor: ActorRef = context.actorOf(Props[KafkaProducerActor], name = "kafkaProducerActor")
  val calculatingActor: ActorRef = context.actorOf(Props[CalculatingActor], name = "calculatingActor")
  val transformingActor: ActorRef = context.actorOf(Props[TransformingActor], name = "transformingActor")
  val ingestingActor: ActorRef = context.actorOf(Props[IngestingActor], name = "ingestingActor")

  override def receive: Receive = {
    case StartWork =>
      ingestingActor ! IngestDataFromDatasource
      sender() ! WorkStarted
    case CompleteWork =>
      sender() ! WorkCompleted
      ingestingActor ! IngestDataFromDatasource
    case DataIngested(data) => log.info("Data ingested.")
    case UnknownMessage => ingestingActor ! IngestDataFromDatasource
    case _ =>
      log.info("Unknown message. Supervisor.")
      context.parent ! CompleteWork
      sender() ! UnknownMessage
  }
}
