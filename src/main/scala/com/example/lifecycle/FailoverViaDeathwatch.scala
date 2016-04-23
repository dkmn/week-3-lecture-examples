package com.example.lifecycle

import akka.actor.{Actor, ActorLogging, Props, Terminated}

/**
  * This actor uses lifecycle monitoring / deathwatch to implement a primary
  * behavior and a backup behavior.
  */
class FailoverViaDeathwatch extends Actor with ActorLogging {

  def primaryBehavior: Receive = {
    val child = context.actorOf(ThingToWatch.props)
    context.watch(child)

    { case Terminated(thingWatched) => context.become(backupBehavior) }
  }

  def backupBehavior: Receive = ???

  override def receive: Receive = primaryBehavior
}


object ThingToWatch {
  val props = Props[ThingToWatch]
}

class ThingToWatch extends Actor with ActorLogging {
  override def receive: Receive = ???
}
