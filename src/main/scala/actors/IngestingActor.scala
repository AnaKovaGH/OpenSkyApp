package actors


import akka.actor.{Actor, ActorSelection}
import com.typesafe.config.{Config, ConfigFactory}
import io.tmos.arm.ArmMethods.manage
import org.apache.http.HttpEntity
import org.apache.http.client.methods.{CloseableHttpResponse, HttpGet}
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.params.HttpConnectionParams

import scala.concurrent.duration.Duration
import scala.jdk.DurationConverters.JavaDurationOps
import scala.util.control.NonFatal
import messages.{IngestDataMessage, TransformDataToJSONMessage}


class IngestingActor() extends Actor {
  val transformingActor: ActorSelection = context.actorSelection("/user/SupervisorActor/transformingActor")

  val config: Config = ConfigFactory.load("OpenSky.conf")
  val url: String = config.getString("osc.api-url")
  val connectTimeout:  Duration = config.getDuration("osc.connect-timeout").toScala
  val readTimeout:  Duration = config.getDuration("osc.read-timeout").toScala
  val socketTimeout:  Duration = config.getDuration("osc.socket-timeout").toScala

  def buildHttpClient(): DefaultHttpClient = {
      val httpClient = new DefaultHttpClient
      val httpParams = httpClient.getParams
      HttpConnectionParams.setConnectionTimeout(httpParams, connectTimeout.toMillis.toInt)
      HttpConnectionParams.setSoTimeout(httpParams, socketTimeout.toMillis.toInt)
      HttpConnectionParams.setSoTimeout(httpParams, readTimeout.toMillis.toInt)
      httpClient.setParams(httpParams)
      httpClient
  }

  val httpClient: DefaultHttpClient = buildHttpClient()
  manage(httpClient)

  override def receive: Receive = {
    case IngestDataMessage =>
      val ingestedData: String = ingestData()
      transformingActor ! TransformDataToJSONMessage(ingestedData)
    case _ => println("Unknown message. Did not start ingesting data. IngestingActor")
  }

  def ingestData(): String = {
    try {
      val httpResponse: CloseableHttpResponse = httpClient.execute(new HttpGet(url))
      val entity: HttpEntity = httpResponse.getEntity
      var content = ""
      if (entity != null) {
        val inputStream = entity.getContent
        manage(inputStream)
        content = scala.io.Source.fromInputStream(inputStream).mkString
      }
      content
    }
    catch {
      case NonFatal(error) => error.printStackTrace().toString
    }
  }
}
