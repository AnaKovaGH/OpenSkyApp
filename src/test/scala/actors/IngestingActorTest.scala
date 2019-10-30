package actors


import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}

import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import messages.{DataIngested, IngestDataFromDatasource, UnknownMessage}


class IngestingActorTest extends TestKit(ActorSystem("testSystem"))
  with ImplicitSender
  with WordSpecLike
  with BeforeAndAfterAll
  with Matchers {

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "An IngestingActor" should {
    val ingestingActor = system.actorOf(Props[IngestingActor], name = "ingestingActor")
    val probe = TestProbe()

    "send back UnknownMessage" in {
      probe.send(ingestingActor, "hi")
      probe.expectMsg(UnknownMessage)
    }

    "send back ingested data from data source" in {
      probe.send(ingestingActor, IngestDataFromDatasource)
      probe.expectMsgType[DataIngested]
    }
  }
}


