package main

import actors.{CalculatingActor, IngestingActor, SendingKafkaActor, TransformingActor}
import akka.actor.{ActorSystem, Props}

object Main extends App {
  val actorSystem = ActorSystem("testSystem")

  val ingestingActor = actorSystem.actorOf(Props[IngestingActor], "IngestingActor")
  val transformingActor = actorSystem.actorOf(Props[TransformingActor], "TransformingActor")
  val calculatingActor = actorSystem.actorOf(Props[CalculatingActor], "CalculatingActor")
  val sendingKafkaActor = actorSystem.actorOf(Props[SendingKafkaActor], "SendingKafkaActor")

  transformingActor ! 10

  actorSystem.terminate()
}
