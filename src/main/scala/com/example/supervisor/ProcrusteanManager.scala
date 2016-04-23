package com.example.supervisor

import akka.actor.SupervisorStrategy.{Escalate, Restart, Stop}
import akka.actor.{Actor, ActorLogging, AllForOneStrategy}

import scala.concurrent.duration._

/**
  * <p>An actor using an AllForOneStrategy.  An AllForOneStrategy will apply the
  * result of the Decider to all of the supervisor's children.  Such strategies
  * can be useful when the children are collaborating on some task for which
  * progress or success is vitiated hopelessly by the failure of any one of
  * them.</p>
  *
  * <p>The descriptive remarks in the CarefulManager example apply in the same
  * sense here.</p>
  *
  */
class ProcrusteanManager extends Actor with ActorLogging {

  override val supervisorStrategy =
                                  AllForOneStrategy(maxNrOfRetries = 5,
                                                    withinTimeRange = 5 seconds,
                                                    loggingEnabled = true) {
    case _: IllegalStateException => Restart
    case _: IllegalArgumentException => Stop
    case _: NullPointerException => Stop
    case _: Exception => Escalate
  }

  override def receive: Receive = ???
}
