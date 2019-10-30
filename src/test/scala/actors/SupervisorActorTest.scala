package actors


import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}

import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import messages.{CompleteWork, StartWork, UnknownMessage, WorkCompleted, WorkStarted}


class SupervisorActorTest extends TestKit(ActorSystem("testSystem"))
  with ImplicitSender
  with WordSpecLike
  with BeforeAndAfterAll
  with Matchers {

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "A SupervisorActor" should {
    val supervisorActor = system.actorOf(Props[SupervisorActor], name = "supervisorActor")
    val probe = TestProbe()

    "send back UnknownMessage" in {
      probe.send(supervisorActor, "hi")
      probe.expectMsg(UnknownMessage)
    }

    "send back WorkStarted" in {
      probe.send(supervisorActor, StartWork)
      probe.expectMsg(WorkStarted)
    }

    "send back WorkCompleted" in {
      probe.send(supervisorActor, CompleteWork)
      probe.expectMsg(WorkCompleted)
    }
  }
}