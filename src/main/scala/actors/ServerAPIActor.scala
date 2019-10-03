package actors

import akka.actor.Actor
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer

import com.typesafe.config.ConfigFactory
import scala.concurrent.Future

import messages.SendCalculatedDataMessage


class ServerAPIActor() extends Actor {
  override def receive: Receive = {
    case SendCalculatedDataMessage(data) =>
      routes(data)
  }

  def routes(data: Map[String, Double]) = {
    val config = ConfigFactory.load("OpenSky.conf").getConfig("http")
    val host = config.getString("host")
    val port = config.getInt("port")

    implicit val materializer: ActorMaterializer = ActorMaterializer()

    val route = {
      get {
        complete(data.toString)
      }
    }

    val serverBinding: Future[Http.ServerBinding] = Http(context.system).bindAndHandle(route, host, port)
  }
}