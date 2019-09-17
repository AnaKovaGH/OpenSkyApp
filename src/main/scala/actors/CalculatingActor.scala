package actors


import akka.actor.{Actor, ActorSelection}

import messages.{CalculateDataMessage, SendDataToKafkaMessage}


class CalculatingActor() extends Actor {
  val sendingKafkaActor: ActorSelection = context.actorSelection("/user/SupervisorActor/sendingKafkaActor")

  override def receive: Receive = {
    case CalculateDataMessage(transformedData) =>
      val calculatedData: String = calculateData(transformedData)
      sendingKafkaActor ! SendDataToKafkaMessage(calculatedData)
    case _ => println("Unknown message. Did not start calculating data. CalculatingActor.")
  }

  def calculateData(data: String): String = {
    "There will be computing"
  }
}