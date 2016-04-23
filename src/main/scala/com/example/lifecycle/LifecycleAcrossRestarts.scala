package com.example.lifecycle

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorLogging, ActorRef}
import com.example.lifecycle.LifecycleAcrossRestarts.{StartDoingSomethingWithMe, StopDoingSomethingWithMe}

/**
  * This actor illustrates a useful trick.
  *
  * You cannot keep state local to the actor across restarts but if there's
  * external state that's passed in via the actor's constructor you can
  * hook preStart and postStop to make use of that state.
  *
  * You'd create such actors with:
  *
  *   actorOf(Props(new LifecycleAcrossRestarts(someActorRef))
  *
  * to provide the constructor argument that's required.  The Props used will
  * be remembered by the actor system, thus allowing the actor to be
  * restarted.
  */
object LifecycleAcrossRestarts {
  case class StartDoingSomethingWithMe(a: ActorRef)
  case class StopDoingSomethingWithMe(a: ActorRef)
}

class LifecycleAcrossRestarts(somebody: ActorRef) extends Actor
                                                  with ActorLogging {

  /*
   * Here we hook preStart to set something up and postStop to clear it out.
   */
  override def preStart() { somebody ! StartDoingSomethingWithMe(self)}
  override def postStop() { somebody ! StopDoingSomethingWithMe(self)}

  /*
   * You can get rid of the default behavior of hooks as well.  It is suggested
   * that you read the source code for the (short) default implementations of
   * the following two hooks in the Actor trait.
   */
  override def preRestart(t: Throwable, message: Option[Any]) {}
  override def postRestart(t: Throwable) {}

  override def receive: Receive = ???
}
