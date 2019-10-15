package actors


import akka.actor.{Actor, ActorLogging, ActorSelection}
import io.circe._
import io.circe.parser._
import messages.{CalculateData, CompleteWork, TransformDataToJSON, UnknownMessage}


class TransformingActor() extends Actor with ActorLogging {
  val calculatingActor: ActorSelection = context.actorSelection("/user/SupervisorActor/calculatingActor")

  override def receive: Receive = {
    case TransformDataToJSON(ingestedData) =>
      val transformedData: Option[Json] = transformDataToJSON(ingestedData)
      transformedData match {
        case Some(value) => calculatingActor ! CalculateData(value)
        case None => context.parent ! CompleteWork
      }
    case UnknownMessage => context.parent ! CompleteWork
    case _ =>
      log.info("Unknown message. Did not start transforming data. TransformingActor.")
      sender ! UnknownMessage
  }

  def transformDataToJSON(ingestedData: String): Option[Json] = {
    val jsonData: Either[ParsingFailure, Json] = parse(ingestedData)
    jsonData match {
      case Right(value) => Some(value)
      case Left(value) => None
    }
  }
}
