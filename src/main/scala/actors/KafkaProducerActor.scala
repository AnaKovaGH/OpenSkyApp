package actors


import akka.actor.Actor
import com.typesafe.config.{Config, ConfigFactory}

import io.tmos.arm.ArmMethods.manage
import io.circe.Json

import java.util.Properties
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import scala.util.control.NonFatal

import messages.{CompleteWork, SendDataToKafka}


class KafkaProducerActor() extends Actor {
  val config: Config = ConfigFactory.load("OpenSky.conf").getConfig("kafkaconfig")
  val props: Properties = new Properties()
  props.put("bootstrap.servers", config.getString("bootstrap-servers"))
  props.put("key.serializer", config.getString("key-serializer"))
  props.put("value.serializer", config.getString("value-serializer"))
  props.put("acks", config.getString("acks"))

  val producer = new KafkaProducer[String, String](props)
  manage(producer)

  override def receive: Receive = {
    case SendDataToKafka(calculatedData) =>
      sendDataToKafka(calculatedData)
      context.parent ! CompleteWork
    case _ => println("Unknown message. Did not start sending data. SendingKafkaActor.")
  }

  def sendDataToKafka(data: Json): Option[String]  = {
    val topic = config.getString("topic")
    try {
      val record = new ProducerRecord[String, String](topic, data.toString)
      producer.send(record)
      Some("Done sending to Kafka!")
    }
    catch {
      case NonFatal(error) =>
        error.printStackTrace()
        None
    }
  }
}