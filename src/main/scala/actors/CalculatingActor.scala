package actors


import akka.actor.{Actor, ActorSelection}
import io.circe.{HCursor, Json}
import scala.util.Try
import com.typesafe.config.{Config, ConfigFactory}

import scala.collection.mutable.Map
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

    def extractDouble (item: Json): List[String] = {
      val cursor: HCursor = item.hcursor
      val list = cursor.values.get.toList
      val result = list.map(_.toString)
      result
    }

    def findHighestAltitude(data: Option[(Json, List[Json])]): Option[Double] = {
      try {
        data match {
          case Some(value) =>
            val states = value._2
            val listOfAltitudes = states.map({item => val tmp = extractDouble(item); tmp(altitudeIndex)})
            val maxAltitude = listOfAltitudes.flatMap(item => Try(item.toDouble).toOption).max
            Some(maxAltitude)
          case _ => None
        }
      }
      catch {
        case error: Exception => None
      }
    }

  def findHighestSpeed(data: Option[(Json, List[Json])]): Option[Double] = {
    try {
      data match {
        case Some(value) =>
          val states = value._2
          val listOfAltitudes = states.map({item => val tmp = extractDouble(item); tmp(speedIndex)})
          val maxAltitude = listOfAltitudes.flatMap(item => Try(item.toDouble).toOption).max
          Some(maxAltitude)
        case _ => None
      }
    }
    catch {
      case error: Exception => None
    }
  }

  def findBorders(data: List[Float]) = {
    val radius: Double = config.getDouble("airportsconfig.radius")
    var result = Map.empty[String, Float]

    result += "lamin" -> (data.head - radius).toFloat
    result += "lamax" -> (data.head + radius).toFloat
    result += "lomin" -> (data.last - radius).toFloat
    result += "lomax" -> (data.last + radius).toFloat

    result
  }

  def findCountOfAirplanes(data: Option[(Json, List[Json])]) = {
    val airport1: List[Float] = config.getString("airportsconfig.airport1").split(", ").toList.map(_.toFloat)
    val airport2: List[Float] = config.getString("airportsconfig.airport2").split(", ").toList.map(_.toFloat)
    val listOfAiports: List[List[Float]] = List(airport1, airport2)

    try {
      data match {
        case Some(value) =>
          val states = value._2
          val list = states.map({item => val tmp = extractDouble(item); (Try(tmp(airplaneLattitudeIndex).toDouble).toOption, Try(tmp(airplaneLongtitudeIndex).toDouble).toOption) })

          val tmp = listOfAiports.map(findBorders)

          list.filter(item => item._1 == 1 && item._2 == 5)

          //list.filter(item => item._1 == 1 && item._2 == 5)


        case _ => None
      }
    }
    catch {
      case error: Exception => None
    }
    1
  }

}







//  def findCountOfAirplanes(data: List[Json]): Int = {
//    val config: Config = ConfigFactory.load("OpenSky.conf")
//
//    val radius: Double = config.getDouble("airportsconfig.radius")
//    val airport1: List[Float] = config.getString("airportsconfig.airport1").split(", ").toList.map(_.toFloat)
//    val airport2: List[Float] = config.getString("airportsconfig.airport2").split(", ").toList.map(_.toFloat)
//    val listOfAiports: List[List[Float]] = List(airport1, airport2)
//
//    val states= data
//    val airplaneLongIndex: Int = 5
//    val airplaneLattIndex: Int = 6
//
//    var count: Int = 0
//    var buffer = Map[List[Float], Int]
//
//    for( airport <- listOfAiports ){
//      val lamin: Float = (airport.head - radius).toFloat
//      val lamax: Float = (airport.head + radius).toFloat
//      val lomin: Float = (airport.last - radius).toFloat
//      val lomax: Float = (airport.last + radius).toFloat
//
//      for( item <- states ) {
//        val cursor: HCursor = item.hcursor
//        val list: List[Json] = cursor.values.get.toList
//        val longtitude: Float = list(airplaneLongIndex).toString.toFloat
//        val lattitude: Float = list(airplaneLattIndex).toString.toFloat
//
//        if (longtitude <= lomin && longtitude >= lomax){
//          if (lattitude <= lamin && lattitude >= lamax){
//            count += 1
//          }
//        }
//      }
//     // buffer += (airport -> count)
//    }
//    1
//  }
//
//  def wrapper(HighestAttitude: Int, HighestSpeed:Int, CountOfAirplanes:Int): String = {
//    "Wrap all results for sending to Kafka" //!TEMPORARY!
//  }
//}