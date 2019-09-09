package actors

import akka.actor._

class SendingKafkaActor extends Actor {
  override def receive: Receive = {
    case _ => println("Send to Kafka")
  }
}