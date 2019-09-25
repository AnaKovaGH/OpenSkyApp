package actors


import akka.actor.{Actor, ActorSelection}
import com.typesafe.config.{Config, ConfigFactory}
import collection.JavaConverters._
import io.circe.{HCursor, Json}
import scala.util.Try

import messages.{CalculateDataMessage, SendDataToKafkaMessage}


class CalculatingActor() extends Actor {
  val sendingKafkaActor: ActorSelection = context.actorSelection("/user/SupervisorActor/sendingKafkaActor")

  val config: Config = ConfigFactory.load("OpenSky.conf")

  val altitudeIndex: Int = 7
  val speedIndex: Int = 9
  val airplaneLongtitudeIndex: Int = 5
  val airplaneLattitudeIndex: Int = 6

  override def receive: Receive = {
    case CalculateDataMessage(data) =>
      val extractedData = extractData(data)
      val highestAltitude = findHighestAltitude(extractedData)
      val highestSpeed = findHighestSpeed(extractedData)
      val CountOfAirplanes = findCountOfAirplanes(extractedData)
      //val results: String = wrapper(HighestAltitude, HighestSpeed, CountOfAirplanes) //!TEMPORARY!
      sendingKafkaActor ! SendDataToKafkaMessage("test sending") //SendDataToKafkaMessage(results)

    case _ => println("Unknown message. Did not start calculating data. CalculatingActor.")
  }

  def extractData(data: Json): Option[(Json, List[Json])] = {
    try {
      val timestamp = data.findAllByKey("time").head
      val cursor: HCursor = data.hcursor
      val states = cursor.downField("states").values.map(_.toList)
      states match {
        case Some(value) => Some(timestamp, value)
        case _ => None
      }
    }
    catch {
      case error: Exception => None
    }
  }

  def extractStateList(item: Json): List[String] = {
    val cursor: HCursor = item.hcursor
    val listWithJsonValues = cursor.values.get.toList
    val listWithStringValues = listWithJsonValues.map(_.toString)
    listWithStringValues
  }

  def findHighestAltitude(data: Option[(Json, List[Json])]): Option[Double] = {
    try {
      val values = data.getOrElse({
        return None
      })
      val states = values._2
      val listOfAltitudes = states.map({ item =>
        val oneStateList = extractStateList(item)
        oneStateList(altitudeIndex)
      })
      val maxAltitude = listOfAltitudes.flatMap(item => Try(item.toDouble).toOption).max
      Some(maxAltitude)
    }
    catch {
      case error: Exception => None
    }
  }

  def findHighestSpeed(data: Option[(Json, List[Json])]): Option[Double] = {
    try {
      val values = data.getOrElse({
        return None
      })
      val states = values._2
      val listOfSpeed = states.map({ item =>
        val oneStateList = extractStateList(item)
        oneStateList(speedIndex)
      })
      val maxSpeed = listOfSpeed.flatMap(item => Try(item.toDouble).toOption).max
      Some(maxSpeed)
    }
    catch {
      case error: Exception => None
    }
  }

  def findCoordinatesBorders(data: List[Float]): Map[String, Float] = {
    val radius: Double = config.getDouble("airportsconfig.radius")
    val mapWithCoordinatesBorders: Map[String, Float] = Map (
      "lamin" -> (data.head - radius).toFloat,
      "lamax" -> (data.head + radius).toFloat,
      "lomin" -> (data.last - radius).toFloat,
      "lomax" -> (data.last + radius).toFloat
    )
    mapWithCoordinatesBorders
  }

  def findCountOfAirplanes(data: Option[(Json, List[Json])]): Option[Int] = {
    val airports = config.getConfigList("airportsconfig.airports").asScala.toList
    val minMeasure = -300.0 //to move latt or long if it is null

    val listOfPlanesByAirport = airports.map{ airport =>
      val airportLatitude = airport.getString("lat").toDouble
      val airportLongtitude = airport.getString("long").toDouble
      val radius: Double = config.getDouble("airportsconfig.radius")

      val planeStates = data.getOrElse({
        return None
      })._2.map({
        plane =>
          var lat: Double = {
            if (extractStateList(plane)(airplaneLongtitudeIndex) == "null") {
              minMeasure//out of scope of latitudides (min lat == -180)
            }
            else {
              extractStateList(plane)(airplaneLongtitudeIndex).toDouble
            }
          }
          var long: Double = {
            if (extractStateList(plane)(airplaneLattitudeIndex) == "null") {
              minMeasure //out of scope of longtitudes (min long == -90)
            }
            else {
              extractStateList(plane)(airplaneLattitudeIndex).toDouble
            }
          }
          Map("lat" -> lat, "long" -> long)
      })

      planeStates.filter(
        _ ("lat") >= airportLatitude - radius) //lamin
        .filter(_ ("lat") <= airportLatitude + radius) //lamax
        .filter(_ ("long") >= airportLongtitude - radius) //lomin
        .filter(_ ("long") <= airportLongtitude + radius) //lomin
        .size
    }
    val countOfAllAirplanes = listOfPlanesByAirport.sum
    println(countOfAllAirplanes)
    Some(countOfAllAirplanes)
  }
}

//  def wrapper(HighestAttitude: Int, HighestSpeed:Int, CountOfAirplanes:Int): String = {
//    "Wrap all results for sending to Kafka" //!TEMPORARY!
//  }
//}
