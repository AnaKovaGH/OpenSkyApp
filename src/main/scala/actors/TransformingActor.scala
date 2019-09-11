package actors

import akka.actor.{Actor, ActorRef}
import messages.{CalculateDataMessage, CompleteWork, TransformDataToJSONMessage}
import io.circe._
import io.circe.parser._


class TransformingActor(calculatingActor: ActorRef) extends Actor {
  override def receive: Receive = {
    case TransformDataToJSONMessage(ingestedData) =>
      val transformedData: Json = transformDataToJSON(ingestedData)
      if (transformedData == null)
        context.parent ! CompleteWork
      else
        calculatingActor ! CalculateDataMessage(transformedData)
    case _ => println("Unknown message. Did not start transforming data. TransformingActor.")
  }

  def transformDataToJSON(ingestedData: String): Json = {
    val jsonData = parse(ingestedData).getOrElse(Json.Null)
    jsonData
  }
}