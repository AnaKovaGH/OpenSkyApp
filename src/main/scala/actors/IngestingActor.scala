package actors

import akka.actor.Actor
import com.typesafe.config.{Config, ConfigFactory}
import javax.script.ScriptException



class IngestingActor extends Actor {

  val config: Config = ConfigFactory.load("OpenSky.conf")
  val url: String = config.getString("osc.apiurl")
  val connectTimeout: Int = config.getInt("osc.connectTimeout")
  val readTimeout: Int = config.getInt("osc.readTimeout")
  val requestMethod: String = config.getString("osc.requestMethod")

  override def receive: Receive = {
    case _ => ingestData(url)
  }

  def ingestData(url: String,
          connectTimeout: Int = connectTimeout,
          readTimeout: Int = readTimeout,
          requestMethod: String = requestMethod): String =
  {
    try {
      import java.net.{URL, HttpURLConnection}
      val connection = new URL(url).openConnection.asInstanceOf[HttpURLConnection]
      connection.setConnectTimeout(connectTimeout)
      connection.setReadTimeout(readTimeout)
      connection.setRequestMethod(requestMethod)
      val inputStream = connection.getInputStream
      val content = io.Source.fromInputStream(inputStream).mkString
      if (inputStream != null) inputStream.close()
      connection.disconnect()
      println(content)
      content
    }
    catch {
      case e: ScriptException => e.printStackTrace()
      "Error"
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
