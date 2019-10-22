package actors


import akka.actor.{Actor, ActorLogging, ActorSelection}
import com.typesafe.config.{Config, ConfigFactory}
import io.tmos.arm.ArmMethods.manage

import org.apache.http.HttpEntity
import org.apache.http.client.methods.{CloseableHttpResponse, HttpGet}
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.params.HttpConnectionParams

import scala.concurrent.duration.Duration
import scala.jdk.DurationConverters.JavaDurationOps
import scala.util.control.NonFatal

import messages.{CompleteWork, DataIngested, DataTransformed, IngestDataFromDatasource, TransformDataToJSON, UnknownMessage}


class IngestingActor() extends Actor with ActorLogging {
  val transformingActor: ActorSelection = context.actorSelection("/user/SupervisorActor/transformingActor")

  val config: Config = ConfigFactory.load("OpenSky.conf")
  val url: String = config.getString("osc.api-url")
  val connectTimeout:  Duration = config.getDuration("osc.connect-timeout").toScala
  val readTimeout:  Duration = config.getDuration("osc.read-timeout").toScala
  val socketTimeout:  Duration = config.getDuration("osc.socket-timeout").toScala

  def buildHttpClient(): DefaultHttpClient = {
      val httpClient: DefaultHttpClient = new DefaultHttpClient
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
    case IngestDataFromDatasource =>
      val ingestedData: Option[String] = ingestData()
      ingestedData match {
        case Some(value) =>
          transformingActor ! TransformDataToJSON(value)
          sender() ! DataIngested(value)
        case None =>
          log.info("Data was not ingested.")
          context.parent ! CompleteWork
      }
    case DataTransformed(data) => log.info("Data transformed.")
    case UnknownMessage => context.parent ! CompleteWork
    case _ =>
      log.info("Unknown message. Did not start ingesting data. IngestingActor")
      sender() ! UnknownMessage
  }

  def ingestData(): Option[String] = {
    try {
      val httpResponse: CloseableHttpResponse = httpClient.execute(new HttpGet(url))
      val entity: HttpEntity = httpResponse.getEntity
      var content = ""
      if (entity != null) {
        val inputStream = entity.getContent
        manage(inputStream)
        content = scala.io.Source.fromInputStream(inputStream).mkString
      }
      Some(content)
    }
    catch {
      case NonFatal(error) =>
        error.printStackTrace()
        None
    }
  }
}
