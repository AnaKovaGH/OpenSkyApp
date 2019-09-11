package actors

import akka.actor.{Actor, ActorRef}
import messages.{CalculateDataMessage, SendDataToKafkaMessage}

class CalculatingActor(sendingKafkaActor: ActorRef) extends Actor {
  override def receive: Receive = {
    case CalculateDataMessage(transformedData) =>
      val calculatedData: String = calculateData("test")//(transformedData)
      sendingKafkaActor ! SendDataToKafkaMessage(calculatedData)
    case _ => println("Unknown message. Did not start calculating data. CalculatingActor.")
  }

  def calculateData(data: String): String = {
    "There will be computing"
  }
}