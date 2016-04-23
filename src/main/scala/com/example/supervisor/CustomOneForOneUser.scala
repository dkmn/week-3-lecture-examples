package com.example.supervisor

import akka.actor.SupervisorStrategy.{Restart, Stop}
import akka.actor.{Actor, ActorLogging, ActorRef, OneForOneStrategy}

/**
  * <p>An example actor that implements its own OneForOneStrategy that counts
  * restarts for each child separately and stops a child that has restarted
  * too many times.</p>
  *
  * <p>It should be clear how you would generalize this to allow some children
  * to be allowed more restarts than others, how you might handle certain child
  * failures differently by Escalating or the like.  If your children are all
  * doing different jobs as part of some related task, perhaps it makes sense
  * to handle their failures differently.</p>
  */
class CustomOneForOneUser extends Actor with ActorLogging {

  // Map that keeps track of how often a given child has been restarted
  var restarts = Map.empty[ActorRef, Int].withDefaultValue(0)

  // Using the default parameters for a SupervisorStrategy mean "arbitarily
  // often over forever"
  override val supervisorStrategy = OneForOneStrategy() {

    case _: ArithmeticException =>

      restarts(sender) match {
        case tooManyRestarts if tooManyRestarts > 15 =>
          restarts -= sender
          Stop
        case n =>
          restarts = restarts.updated(sender, n+1)
          Restart
      }
  }

  override def receive: Receive = ???
}
