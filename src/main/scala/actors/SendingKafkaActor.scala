package actors

import akka.actor.Actor
import messages.{CompleteWork, SendDataToKafkaMessage}

import java.util.Properties
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}


class SendingKafkaActor() extends Actor {
  override def receive: Receive = {
    case SendDataToKafkaMessage(calculatedData) =>
      sendDataToKafka(calculatedData)
      context.parent ! CompleteWork
    case _ => println("Unknown message. Did not start sending data. SendingKafkaActor.")
  }
  def sendDataToKafka(data: String): String = {
    val props:Properties = new Properties()
    props.put("bootstrap.servers","localhost:9092")
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("acks","all")

    val producer = new KafkaProducer[String, String](props)
    val topic = "openskystates"
    try {
      val record = new ProducerRecord[String, String](topic, data)
      val metadata = producer.send(record)
      println(metadata)
    }catch{
      case e:Exception => e.printStackTrace()
    }finally {
      producer.close()
    }

    "Test sending"
  }
}