package actors


import akka.actor.{Actor, ActorSelection}
import io.circe.{HCursor, Json}
import messages.{CalculateDataMessage, SendDataToKafkaMessage}


class CalculatingActor() extends Actor {
  val sendingKafkaActor: ActorSelection = context.actorSelection("/user/SupervisorActor/sendingKafkaActor")

  override def receive: Receive = {
    case CalculateDataMessage(transformedData) =>
      val HighestAttitude: Int = findHighestAltitude(transformedData)
      val HighestSpeed:Int = findHighestSpeed(transformedData)
      val CountOfAirplanes:Int = findCountOfAirplanes(transformedData)
      val results: String = wrapper(HighestAttitude, HighestSpeed, CountOfAirplanes) //!TEMPORARY!
      sendingKafkaActor ! SendDataToKafkaMessage(results)
    case _ => println("Unknown message. Did not start calculating data. CalculatingActor.")
  }

  def findHighestAltitude(data: Json): Int = {
    try {
      val timestamp = data.findAllByKey("time").head

      val cursor: HCursor = data.hcursor
      val states= cursor.downField("states").values.get.toList  // List[Json] like ( [], [], [], [] )

      var maxAltitude: Double = 0

      for( item <- states ){
        val cursor: HCursor = item.hcursor
        val list: List[Json] = cursor.values.get.toList
        val altitude: Json = list(7)

        if (altitude.isNumber){
          val number: Double = altitude.toString.toDouble
          if (number > maxAltitude) {
            maxAltitude = number
          }
        }
      }
      println(timestamp + ": " + maxAltitude)
    }
    catch {
      case error: Exception => error.printStackTrace()
    }

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