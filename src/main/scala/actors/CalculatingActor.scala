package actors


import akka.actor.{Actor, ActorSelection}
import io.circe.Json

import messages.{CalculateDataMessage, SendDataToKafkaMessage}


class CalculatingActor() extends Actor {
  val sendingKafkaActor: ActorSelection = context.actorSelection("/user/SupervisorActor/sendingKafkaActor")

  override def receive: Receive = {
    case CalculateDataMessage(transformedData) =>
      val HighestAttitude: Int = findHighestAttitude(transformedData)
      val HighestSpeed:Int = findHighestSpeed(transformedData)
      val CountOfAirplanes:Int = findCountOfAirplanes(transformedData)
      val results: String = wrapper(HighestAttitude, HighestSpeed, CountOfAirplanes) //!TEMPORARY!
      sendingKafkaActor ! SendDataToKafkaMessage(results)
    case _ => println("Unknown message. Did not start calculating data. CalculatingActor.")
  }

  def findHighestAttitude(data: Json): Int = {
    1//!TEMPORARY!
  }

  def findHighestSpeed(data: Json): Int = {
    1//!TEMPORARY!
  }

  def findCountOfAirplanes(data: Json): Int = {
    1//!TEMPORARY!
  }

  def wrapper(HighestAttitude: Int, HighestSpeed:Int, CountOfAirplanes:Int): String = {
    "Wrap all results for sending to Kafka" //!TEMPORARY!
  }
}