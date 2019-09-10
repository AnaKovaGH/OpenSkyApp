package actors

import akka.actor.{Actor, ActorRef}
import messages.SendDataToKafkaMessage

class SendingKafkaActor(supervisorActor: ActorRef) extends Actor {
  override def receive: Receive = {
    case SendDataToKafkaMessage(calculatedData) =>
      val result = "Done sending"
      supervisorActor ! result
    case _ => println("Unknown message. Did not start sending data. SendingKafkaActor.")
  }
  def sendDataToKafka(data: String): String = {
    "There will be sending"
  }
}