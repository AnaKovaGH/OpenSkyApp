package actors

import akka.actor.{Actor, ActorRef}
import messages.SendDataToKafkaMessage

class SendingKafkaActor extends Actor {
  override def receive: Receive = {
    case SendDataToKafkaMessage(calculatedData) => println("Done sending")
    case _ => println("Unknown message. Did not start sending data. SendingKafkaActor.")
  }
  def sendDataToKafka(data: String): String = {
    "There will be sending"
  }
}