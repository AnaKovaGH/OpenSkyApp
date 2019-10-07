package actors


import akka.actor.{Actor, ActorRef, Props}
import messages.{CalculateDataMessage, CompleteWork, IngestDataMessage, SendCalculatedDataMessage, StartMessage, TestRest}


class SupervisorActor extends Actor {
  val sendingKafkaActor: ActorRef = context.actorOf(Props[SendingKafkaActor], name = "sendingKafkaActor")
  val calculatingActor: ActorRef = context.actorOf(Props[CalculatingActor], name = "calculatingActor")
  val transformingActor: ActorRef = context.actorOf(Props[TransformingActor], name = "transformingActor")
  val ingestingActor: ActorRef = context.actorOf(Props[IngestingActor], name = "ingestingActor")

  override def receive: Receive = {
    case StartMessage => ingestingActor ! IngestDataMessage//; sender ! "ok"
    case CompleteWork =>
      println("Work is completed")
      ingestingActor ! IngestDataMessage
    case SendCalculatedDataMessage(data) =>
      context.parent ! data
    case TestRest => sender() ! "ok"
    case _ => println("Unknown message. Supervisor.")
  }
}
