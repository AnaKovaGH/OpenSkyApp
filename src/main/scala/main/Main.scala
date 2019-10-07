package main


import akka.actor.{ActorRef, ActorSystem, Props}
import actors.SupervisorActor
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives.{complete, get, host}
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import messages.{StartMessage, TestRest}

import scala.concurrent.{Await, ExecutionContext, Future}
//import akka.pattern._
//
//import scala.concurrent.duration._
//import akka.util.Timeout
//
//import scala.language.postfixOps


object Main extends App {
  val config = ConfigFactory.load("OpenSky.conf").getConfig("http")
  val host = config.getString("host")
  val port = config.getInt("port")

  implicit val actorSystem: ActorSystem = ActorSystem("testSystem")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = actorSystem.dispatcher

  val supervisorActor: ActorRef = actorSystem.actorOf(Props[SupervisorActor], "SupervisorActor")
  supervisorActor ! StartMessage

//  implicit val duration: Timeout = 20 seconds
//  val res = supervisorActor ? TestRest
//  val result2 = Await.result(res, 5000000 seconds)
//  println(result2)

  val route = {
    get {


      complete("hel")
    }
  }

  val serverBinding: Future[Http.ServerBinding] = Http().bindAndHandle(route, host, port)
}
