package actors


import akka.actor.{Actor, ActorSelection}

import io.circe._
import io.circe.parser._

import messages.{CalculateDataMessage, CompleteWork, TransformDataToJSONMessage}


class TransformingActor() extends Actor {
  val calculatingActor: ActorSelection = context.actorSelection("/user/SupervisorActor/calculatingActor")

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