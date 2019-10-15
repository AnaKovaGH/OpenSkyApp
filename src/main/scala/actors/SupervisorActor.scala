package actors


import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import messages.{CompleteWork, IngestDataFromDatasource, StartWork, UnknownMessage}


class SupervisorActor extends Actor with ActorLogging {
  val kafkaProducerActor: ActorRef = context.actorOf(Props[KafkaProducerActor], name = "kafkaProducerActor")
  val calculatingActor: ActorRef = context.actorOf(Props[CalculatingActor], name = "calculatingActor")
  val transformingActor: ActorRef = context.actorOf(Props[TransformingActor], name = "transformingActor")
  val ingestingActor: ActorRef = context.actorOf(Props[IngestingActor], name = "ingestingActor")

  override def receive: Receive = {
    case StartWork => ingestingActor ! IngestDataFromDatasource
    case CompleteWork =>
      log.info("Work is completed")
      ingestingActor ! IngestDataFromDatasource
    case UnknownMessage => self ! CompleteWork
    case _ =>
      log.info("Unknown message. Supervisor.")
      sender ! UnknownMessage
  }
}
