package actors

import akka.actor.Actor
import messages.{TransformMessage, TransformedDataMessage}


class TransformingActor extends Actor {
  override def receive: Receive = {
    case TransformMessage(ingestedData) =>
      val transformedData: String = transformDataToJSON(ingestedData)
      sender ! TransformedDataMessage(transformedData)
    case _ => println("Transform")
  }

  def transformDataToJSON(ingestedData: String): String = {
    "There will be transformation"
  }
}