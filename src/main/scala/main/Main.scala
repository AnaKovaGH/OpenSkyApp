package main

import actors.SupervisorActor
import akka.actor.{ActorRef, ActorSystem, Props}
import com.typesafe.config.{Config, ConfigFactory}
import messages.StartMessage

import scala.concurrent.duration.Duration
import scala.jdk.DurationConverters.JavaDurationOps


object Main extends App {
  val config: Config = ConfigFactory.load("OpenSky.conf")
  val waitTime:  Duration = config.getDuration("osc.wait-time").toScala

  val actorSystem = ActorSystem("testSystem")

  val supervisorActor: ActorRef = actorSystem.actorOf(Props[SupervisorActor], "SupervisorActor")
  supervisorActor ! StartMessage

  Thread.sleep(waitTime.toMillis.toInt)
  actorSystem.terminate()
}
