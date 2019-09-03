package sample

import akka.actor._


object Main extends App {
  val actorSystem = ActorSystem("testSystem")

  val ingestingActor = actorSystem.actorOf(Props[IngestingActor], "IngestingActor")
  val transformingActor = actorSystem.actorOf(Props[TransformingActor], "TransformingActor")
  val CalculatingActor = actorSystem.actorOf(Props[CalculatingActor], "CalculatingActor")
  val SendingKafkaActor = actorSystem.actorOf(Props[SendingKafkaActor], "SendingKafkaActor")

  actorSystem.terminate()
}
