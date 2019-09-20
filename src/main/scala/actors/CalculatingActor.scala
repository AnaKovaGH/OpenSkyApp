package actors


import akka.actor.{Actor, ActorSelection}
import io.circe.{Decoder, DecodingFailure, HCursor, Json}
import com.typesafe.config.{Config, ConfigFactory}

import scala.collection.mutable.ListBuffer
import messages.{CalculateDataMessage, CompleteWork, SendDataToKafkaMessage}


class CalculatingActor() extends Actor {
  val sendingKafkaActor: ActorSelection = context.actorSelection("/user/SupervisorActor/sendingKafkaActor")

  val AltitudeIndex: Int = 7
  val SpeedIndex: Int = 9
  val airplaneLongtitudeIndex: Int = 5
  val airplaneLattitudeIndex: Int = 6

  override def receive: Receive = {
    case CalculateDataMessage(transformedData) =>
      val data = extractData(transformedData)
      val HighestAltitude = findHighestAltitude(data)
      //val HighestSpeed: Double = findHighestSpeed(data._2)
      //val CountOfAirplanes: Int = findCountOfAirplanes(data._2)
      //val results: String = wrapper(HighestAltitude, HighestSpeed, CountOfAirplanes) //!TEMPORARY!
      sendingKafkaActor ! SendDataToKafkaMessage("test sending") //SendDataToKafkaMessage(results)


    case _ => println("Unknown message. Did not start calculating data. CalculatingActor.")
  }

  def extractData(data: Json) = {
    try {
      val timestamp = data.findAllByKey("time").head
      val cursor: HCursor = data.hcursor
      val states = cursor.downField("states").values.map(_.toList)
      states match {
        case Some(value) => Some(timestamp, value)
        case none => None
      }
    }
    catch {
      case error: Exception => None
    }
  }

  def extractDouble (item: Json): Option[Double] = {
    val cursor: HCursor = item.hcursor
    val list = cursor.values.get.toList
    val currentNumber: Json = list(7)
    if (currentNumber.isNumber) {
      Some(currentNumber.toString.toDouble)
    }
    else {
      None
    }
  }

  def findHighestAltitude(data: Option[(Json, List[Json])])= {
    try {
      data match {
        case Some(value) =>
          val states = value._2
          val maxAltitude = states.map(extractDouble)
          println (maxAltitude.max)
          Some (maxAltitude)
        case None => None
      }
    }
    catch {
      case error: Exception => None
    }
  }










//  def extractData(data: Json) = {
//    try {
//      val time = data.findAllByKey("time").head
//      val states = data.findAllByKey("states").head
//      Some(time, states)
//    }
//    catch {
//      case error: Exception => None
//    }
//  }
//  def findHighestAltitude(data: Option[(Json, Json)])  = {
//    try {
//      data match {
//        case Some(value) =>
//          val states = value._2
//          val k = states.
//        case None => None
//      }
//    }
//    catch {
//      case error: Exception => None
//    }
//  }


//  def findHighestAltitude(data: Option[(Double, List[Json])])  = {
//    try {
//      data match {
//        case Some(value) =>
//          val states = value._2.map(_.hcursor.values.map(println))
//          //val states = value._2.map(_.map(_.hcursor.values.as))
//          //val tmp = states.map(_.map(_.hcursor.values.toList.map(_(7))))
//          println(states)
//          //println(tmp)
//
//        case None => None
//      }
//
//    }
//    catch  {
//      case error: Exception => None
//    }
//  }

  ////!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!val k = states.map(_.map(_(7)))

//  def extractData(data: Json) =  {
//    try {
//      val timestamp = data.findAllByKey("time").head
//      val cursor: HCursor = data.hcursor
//      val states = cursor.downField("states").values.map(_.toList)//.get.toList // List[Json] like ( [], [], [], [] )
//
//      Some(timestamp, states)
//    }
//    catch {
//      case error: Exception => None
//    }
//  }
//
//  def extractDouble (item: Json): Option[Double] = {
//    val cursor: HCursor = item.hcursor
//    val list = cursor.values.get.toList
//    val currentNumber: Json = list(7)
//    if (currentNumber.isNumber) {
//      Some(currentNumber.toString.toDouble)
//    }
//    else {
//      None
//    }
//  }
//
//
//  def findHighestAltitude(data: Option[List[Json]])= {
//    try {
//      data match {
//        case Some(value) =>
//          val states = data
//          val maxAltitude = states.map(extractDouble)
//          println (maxAltitude)
//
//          Some (maxAltitude)
//      }
//    }
//    catch {
//      case error: Exception => None
//    }
//  }

//  def findHighestSpeed(data: List[Json]): Double = {
//    try {
//      val states = data
//      val index: Int = 9
//
//      val buffer = createBufferListWithNumbers(states, index)
//      val listOfSpeed: List[Double] = buffer.toList
//      val maxSpeed: Double = listOfSpeed.max
//      maxSpeed
//    }
//    catch {
//      case error: Exception =>
//        error.printStackTrace()
//        1.1
//    }
//  }
//
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
}