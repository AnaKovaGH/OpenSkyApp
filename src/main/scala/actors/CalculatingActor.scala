package actors


import akka.actor.{Actor, ActorSelection}
import io.circe.{HCursor, Json}
import scala.collection.mutable.ListBuffer

import messages.{CalculateDataMessage, SendDataToKafkaMessage}


class CalculatingActor() extends Actor {
  val sendingKafkaActor: ActorSelection = context.actorSelection("/user/SupervisorActor/sendingKafkaActor")
  val ingestingActor: ActorSelection = context.actorSelection("/user/SupervisorActor/ingestingActor")

  override def receive: Receive = {
    case CalculateDataMessage(transformedData) =>
      val data: (Json, List[Json]) = extractData(transformedData)
      val HighestAltitude: Double = findHighestAltitude(data._2)
      val HighestSpeed: Double = findHighestSpeed(data._2)
      //val CountOfAirplanes: Int = findCountOfAirplanes(data)
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

  def findCountOfAirplanes(data: (Json, List[Json])): Int = {
    //val config: Config = ConfigFactory.load("OpenSky.conf")
    //val radius: String = config.getString("airportsconfig.radius")
    //val airport1: List[Float] = config.getString("airportsconfig.airport1")

    //test parameters
    val radius: Double = 12345.5
    val airport1: List[Float] = List(49.842957f, 24.031111f) //lat, long
    val airport2: List[Float] = List(50.411198f, 30.446634f)
    val listOfAiports: List[List[Float]] = List(airport1, airport2)

    val timestamp = data._1
    val states= data._2

    for( airport <- listOfAiports ){
      val lamin: Float = (airport.head - radius).toFloat
      val lamax: Float = (airport.head + radius).toFloat
      val lomin: Float = (airport.last - radius).toFloat
      val lomax: Float = (airport.last + radius).toFloat

      for( item <- states ) {
        val cursor: HCursor = item.hcursor
        val list: List[Json] = cursor.values.get.toList
        val longtitude: Float = list(5).toString.toFloat
        val lattitude: Float = list(6).toString.toFloat

        if (longtitude <= lomin && longtitude >= lomax){
          if (lattitude <= lamin && lattitude >= lamax){
            "add params to the url and send it to ingest actor or think how it can be done"
          }
        }
      }
    }
    1//!TEMPORARY!
  }

  def wrapper(HighestAttitude: Int, HighestSpeed:Int, CountOfAirplanes:Int): String = {
    "Wrap all results for sending to Kafka" //!TEMPORARY!
  }
}