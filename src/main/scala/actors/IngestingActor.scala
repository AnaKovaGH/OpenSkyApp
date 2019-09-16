package actors


import akka.actor.{Actor, ActorRef}
import com.typesafe.config.{Config, ConfigFactory}
import messages.{IngestDataMessage, TransformDataToJSONMessage}

import scala.concurrent.duration.Duration
import scala.jdk.DurationConverters.JavaDurationOps
import scala.util.control.NonFatal

import java.net.{URL, HttpURLConnection}
import io.tmos.arm.ArmMethods._


class IngestingActor(transformingActor: ActorRef) extends Actor {
  val config: Config = ConfigFactory.load("OpenSky.conf")
  val url: String = config.getString("osc.api-url")
  val connectTimeout:  Duration = config.getDuration("osc.connect-timeout").toScala
  val readTimeout:  Duration = config.getDuration("osc.read-timeout").toScala
  val requestMethod: String = "GET"

  val connection: HttpURLConnection = new URL(url).openConnection.asInstanceOf[HttpURLConnection]
  connection.setConnectTimeout(connectTimeout.toMillis.toInt)
  connection.setReadTimeout(readTimeout.toMillis.toInt)
  connection.setRequestMethod(requestMethod)

  override def receive: Receive = {
    case IngestDataMessage =>
      val ingestedData: String = ingestData()
      transformingActor ! TransformDataToJSONMessage(ingestedData)
    case _ => println("Unknown message. Did not start ingesting data. IngestingActor")
  }

  def ingestData(): String = {
    try {
      val inputStream = connection.getInputStream
      val content = scala.io.Source.fromInputStream(inputStream).mkString
      println(content)
      content
    }
    catch {
      case NonFatal(error)  => error.printStackTrace().toString
    }
  }

}

//Explanation:
//---------------
//openConnection:
//Returns a URLConnection instance that represents a connection to the remote object referred to by the URL.
//Same as openConnection(), except that the connection will be made through the specified proxy;
// Protocol handlers that do not support proxing will ignore the proxy parameter and make a normal connection.
// Invoking this method preempts the system's default ProxySelector settings.
//---------------
//setConnectTimeout
//Sets a specified timeout value, in milliseconds, to be used when opening a communications link to the resource referenced by this URLConnection.
// If the timeout expires before the connection can be established, a java.net.SocketTimeoutException is raised.
// A timeout of zero is interpreted as an infinite timeout.
//---------------
//setReadTimeout
//Sets the read timeout to a specified timeout, in milliseconds.
// A non-zero value specifies the timeout when reading from Input stream when a connection is established to a resource.
// If the timeout expires before there is data available for read, a java.net.SocketTimeoutException is raised.
// A timeout of zero is interpreted as an infinite timeout.
//---------------
//getInputStream
//Returns an input stream that reads from this open connection.
// A SocketTimeoutException can be thrown when reading from the returned input stream if the read timeout expires before data is available for read.
