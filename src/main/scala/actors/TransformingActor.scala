package actors

import akka.actor.{Actor, ActorRef}
import messages.{CalculateDataMessage, TransformDataToJSONMessage}


class TransformingActor(calculatingActor: ActorRef) extends Actor {
  override def receive: Receive = {
    case TransformDataToJSONMessage(ingestedData) =>
      val transformedData: String = transformDataToJSON(ingestedData)
      calculatingActor ! CalculateDataMessage(transformedData)
    case _ => println("Unknown message. Did not start transforming data. TransformingActor.")
  }

  def transformDataToJSON(ingestedData: String): String = {
    "There will be transformation"
  }
}