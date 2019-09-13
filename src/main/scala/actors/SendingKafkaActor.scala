package actors

import akka.actor.Actor
import messages.{CompleteWork, SendDataToKafkaMessage}
import java.util.Properties

import com.typesafe.config.{Config, ConfigFactory}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}


class SendingKafkaActor() extends Actor {
  override def receive: Receive = {
    case SendDataToKafkaMessage(calculatedData) =>
      sendDataToKafka(calculatedData)
      context.parent ! CompleteWork
    case _ => println("Unknown message. Did not start sending data. SendingKafkaActor.")
  }

  def sendDataToKafka(data: String): String = {
    val config: Config = ConfigFactory.load("OpenSky.conf").getConfig("kafkaconfig")

    val props:Properties = new Properties()
    props.put("bootstrap.servers", config.getString("bootstrap-servers"))
    props.put("key.serializer", config.getString("key-serializer"))
    props.put("value.serializer", config.getString("value-serializer"))
    props.put("acks", config.getString("acks"))

    val producer = new KafkaProducer[String, String](props)
    val topic = "openskystates"
    try {
      val record = new ProducerRecord[String, String](topic, data)
      val metadata = producer.send(record)
    }catch{
      case error:Exception => error.printStackTrace()
    }finally {
      producer.close()
    }
    "Test sending"
  }
}