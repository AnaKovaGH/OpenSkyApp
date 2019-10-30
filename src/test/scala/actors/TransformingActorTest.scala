package actors


import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}

import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import messages.{DataTransformed, TransformDataToJSON, UnknownMessage}


class TransformingActorTest extends TestKit(ActorSystem("testSystem"))
  with ImplicitSender
  with WordSpecLike
  with BeforeAndAfterAll
  with Matchers {

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "An KafkaConsumerActor" should {
    val transformingActor = system.actorOf(Props[TransformingActor], name = "transformingActor")
    val probe = TestProbe()

    "send back UnknownMessage" in {
      probe.send(transformingActor, "hi")
      probe.expectMsg(UnknownMessage)
    }

    "send back transformed data" in {
      val ingestedData = "{\"time\":1571730720,\"states\":[[\"7c35e7\",\"KXL     \",\"Australia\",1571730720," +
        "1571730720,151.023,-31.6746,8534.4,false,194.57,24.19,0,null,8823.96,\"1432\",false,0]]}"
      probe.send(transformingActor, TransformDataToJSON(ingestedData))
      probe.expectMsgType[DataTransformed]
    }
  }
}