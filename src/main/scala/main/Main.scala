package main


import akka.actor.{ActorRef, ActorSystem, Props}
import akka.pattern._
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives.{complete, get}
import akka.stream.ActorMaterializer
import akka.util.Timeout

import actors.{KafkaConsumerActor, SupervisorActor}
import com.typesafe.config.ConfigFactory

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._
import scala.jdk.DurationConverters.JavaDurationOps

import messages.{StartWork, GetDataFromKafka}


object  Main extends App {
  val config = ConfigFactory.load("OpenSky.conf").getConfig("http")
  val host = config.getString("host")
  val port = config.getInt("port")
  implicit val timeout: Timeout = config.getDuration("timeout").toScala
  val duration: Duration = config.getDuration("duration").toScala

  implicit val actorSystem: ActorSystem = ActorSystem("testSystem")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = actorSystem.dispatcher

  val supervisorActor: ActorRef = actorSystem.actorOf(Props[SupervisorActor], "SupervisorActor")
  supervisorActor ! StartWork
  val kafkaConsumerActor: ActorRef = actorSystem.actorOf(Props[KafkaConsumerActor], "KafkaConsumerActor")

  val consumerAnswer = kafkaConsumerActor ? GetDataFromKafka
  val dataFromKafka = Await.result(consumerAnswer, duration).toString

  val route = {
    get {
      complete(dataFromKafka)
    }
  }

  val serverBinding: Future[Http.ServerBinding] = Http().bindAndHandle(route, host, port)
}
