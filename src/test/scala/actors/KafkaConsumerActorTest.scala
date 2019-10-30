package actors


import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}

import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import messages.{GetDataFromKafka, UnknownMessage}


class KafkaConsumerActorTest extends TestKit(ActorSystem("testSystem"))
  with ImplicitSender
  with WordSpecLike
  with BeforeAndAfterAll
  with Matchers {

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "An KafkaConsumerActor" should {
    val probe = TestProbe()
    val kafkaConsumerActor = system.actorOf(Props[KafkaConsumerActor], name = "kafkaConsumerActor")
    val supervisorActor = system.actorOf(Props[SupervisorActor], name = "supervisorActor")

    "send back UnknownMessage" in {
      probe.send(kafkaConsumerActor, "hi")
      probe.expectMsg(UnknownMessage)
    }

    "send back data from Kafka. Data type should be List[String]" in {
      probe.send(kafkaConsumerActor, GetDataFromKafka)
      probe.expectMsgType[List[String]]
    }
  }
}