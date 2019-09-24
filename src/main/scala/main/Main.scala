package main


import akka.actor.{ActorRef, ActorSystem, Props}

import actors.SupervisorActor
import messages.StartMessage


object Main extends App {
  val actorSystem = ActorSystem("testSystem")

  val supervisorActor: ActorRef = actorSystem.actorOf(Props[SupervisorActor], "SupervisorActor")
  supervisorActor ! StartMessage
}
