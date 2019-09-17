package actors

import akka.actor.{Actor, ActorSelection}
import messages.{CalculateDataMessage, TransformDataToJSONMessage}


class TransformingActor() extends Actor {
  val calculatingActor: ActorSelection = context.actorSelection("/user/SupervisorActor/calculatingActor")

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