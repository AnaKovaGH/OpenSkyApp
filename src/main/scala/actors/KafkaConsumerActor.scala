package actors


import akka.actor.Actor
import com.typesafe.config.{Config, ConfigFactory}
import io.tmos.arm.ArmMethods.manage

import java.util
import java.util.Properties

import org.apache.kafka.clients.consumer.KafkaConsumer
import scala.collection.JavaConverters._

import messages.GetDataFromKafka


class KafkaConsumerActor extends Actor {
  val config: Config = ConfigFactory.load("OpenSky.conf").getConfig("kafkaconfig")
  val props:Properties = new Properties()
  props.put("bootstrap.servers", config.getString("bootstrap-servers"))
  props.put("key.deserializer", config.getString("key-deserializer"))
  props.put("value.deserializer", config.getString("value-deserializer"))
  props.put("group.id", config.getString("consumer-group"))

  val consumer = new KafkaConsumer[String, String](props)
  manage(consumer)

  override def receive: Receive = {
    case GetDataFromKafka =>  sender() ! readMessages()
    case _ => println("Unknown message. KafkaConsumer.")
  }

  def readMessages(): List[String] = {
    val topic = config.getString("topic")
    consumer.subscribe(util.Arrays.asList(topic))
    val records = consumer.poll(config.getLong("poll-timeout")).asScala
    records.toList.map(message => message.value().toString)
    }
}
