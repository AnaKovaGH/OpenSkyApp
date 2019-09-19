package actors


import akka.actor.{Actor, ActorSelection}
import io.circe.{HCursor, Json}
import com.typesafe.config.{Config, ConfigFactory}

import scala.collection.mutable.ListBuffer
import messages.{CalculateDataMessage, SendDataToKafkaMessage}


class CalculatingActor() extends Actor {
  val sendingKafkaActor: ActorSelection = context.actorSelection("/user/SupervisorActor/sendingKafkaActor")

  override def receive: Receive = {
    case CalculateDataMessage(transformedData) =>
      val data: (Json, List[Json]) = extractData(transformedData)
      val HighestAltitude: Double = findHighestAltitude(data._2)
      val HighestSpeed: Double = findHighestSpeed(data._2)
      val CountOfAirplanes: Int = findCountOfAirplanes(data._2)
      //val results: String = wrapper(HighestAltitude, HighestSpeed, CountOfAirplanes) //!TEMPORARY!
      sendingKafkaActor ! SendDataToKafkaMessage(HighestAltitude.toString)//SendDataToKafkaMessage(results)


    case _ => println("Unknown message. Did not start calculating data. CalculatingActor.")
  }

  def extractData(data: Json): (Json, List[Json]) = {
      val timestamp = data.findAllByKey("time").head
      val cursor: HCursor = data.hcursor
      val states = cursor.downField("states").values.get.toList // List[Json] like ( [], [], [], [] )
      (timestamp, states)
  }

  def createBufferListWithNumbers(states: List[Json], index: Int): ListBuffer[Double] = {
    var buffer = new ListBuffer[Double]()
    for( item <- states ) {
      val cursor: HCursor = item.hcursor
      val list: List[Json] = cursor.values.get.toList
      val currentNumber: Json = list(index)
      if (currentNumber.isNumber) {
        buffer += currentNumber.toString.toDouble
      }
    }
    buffer
  }

  def findHighestAltitude(data: List[Json]): Double = {
    try {
      val states = data
      val index: Int = 7

      val buffer = createBufferListWithNumbers(states, index)
      val listOfAltitudes: List[Double] = buffer.toList
      val maxAltitude: Double = listOfAltitudes.max
      maxAltitude
    }
    catch {
      case error: Exception =>
        error.printStackTrace()
        1.1 //TODO: find what to return
    }
  }

  def findHighestSpeed(data: List[Json]): Double = {
    try {
      val states = data
      val index: Int = 9

      val buffer = createBufferListWithNumbers(states, index)
      val listOfSpeed: List[Double] = buffer.toList
      val maxSpeed: Double = listOfSpeed.max
      maxSpeed
    }
    catch {
      case error: Exception =>
        error.printStackTrace()
        1.1 //TODO: find what to return
    }
  }

  def findCountOfAirplanes(data: List[Json]): Int = {
    val config: Config = ConfigFactory.load("OpenSky.conf")

    val radius: Double = config.getDouble("airportsconfig.radius")
    val airport1: List[Float] = config.getString("airportsconfig.airport1").split(", ").toList.map(_.toFloat)
    val airport2: List[Float] = config.getString("airportsconfig.airport2").split(", ").toList.map(_.toFloat)
    val listOfAiports: List[List[Float]] = List(airport1, airport2)

    val states= data
    val airplaneLongIndex: Int = 5
    val airplaneLattIndex: Int = 6

    var count: Int = 0
    var buffer = Map[List[Float], Int]

    for( airport <- listOfAiports ){
      val lamin: Float = (airport.head - radius).toFloat
      val lamax: Float = (airport.head + radius).toFloat
      val lomin: Float = (airport.last - radius).toFloat
      val lomax: Float = (airport.last + radius).toFloat

      for( item <- states ) {
        val cursor: HCursor = item.hcursor
        val list: List[Json] = cursor.values.get.toList
        val longtitude: Float = list(airplaneLongIndex).toString.toFloat
        val lattitude: Float = list(airplaneLattIndex).toString.toFloat

        if (longtitude <= lomin && longtitude >= lomax){
          if (lattitude <= lamin && lattitude >= lamax){
            count += 1
          }
        }
      }
      buffer += (airport -> count) //TODO: change type
    }
  }

  def wrapper(HighestAttitude: Int, HighestSpeed:Int, CountOfAirplanes:Int): String = {
    "Wrap all results for sending to Kafka" //!TEMPORARY!
  }
}