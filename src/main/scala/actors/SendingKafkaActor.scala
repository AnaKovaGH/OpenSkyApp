package actors

import akka.actor.Actor
import messages.{CompleteWork, SendDataToKafkaMessage}

class SendingKafkaActor() extends Actor {
  override def receive: Receive = {
    case SendDataToKafkaMessage(calculatedData) => context.parent ! CompleteWork
    case _ => println("Unknown message. Did not start sending data. SendingKafkaActor.")
  }
  def sendDataToKafka(data: String): String = {
    "There will be sending"
  }
}