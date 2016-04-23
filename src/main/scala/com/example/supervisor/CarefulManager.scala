package com.example.supervisor

import akka.actor.SupervisorStrategy.{Escalate, Restart, Stop}
import akka.actor.{Actor, ActorLogging, OneForOneStrategy}

import scala.concurrent.duration._

/**
  * <p>An actor that uses a OneForOneStrategy to monitor its charges.  With
  * a OneForOneStrategy the decision of the supervisor is applied only to the
  * child which has thrown, not to all children.</p>
  *
  * <p>Things to note:</p>
  * <ul>
  *   <li>A OneForOneStrategy applies a <i>fault handling directive</i> that
  *   it gets from a Decider (which is a PartialFunction[Throwable, Directive])
  *   that converts the exception that killed our doomed child into a
  *   Directive.</li>
  *   <li>The Directives in the Decider baked into SupervisorStrategy include
  *   Restart, which recreates the child from its Props and Stop, which
  *   terminates the failed child.</li>
  *   <li>Other Directives include Resume, which resumes processing of the
  *   failed actor, allowing the same actor instance to keep processing messages
  *   and Escalate which escalates the failure to the supervisor
  *   of the supervisor, by rethrowing the cause of the failure; in other words,
  *   the supervisor fails with the same exception that killed the child.</li>
  *   <li>You can specify in the OneForOneStrategy's apply arguments:</li>
  *   <ul>
  *     <li>How many times a given child actor is allowed to be restarted;
  *         if the limit is exceeded the child actor gets stopped.</li>
  *     <li>The duration of the time window for maximum number of retries.</li>
  *   </ul>
  *   <li><b>NOTE</b>The interaction of maxNrOfRetries and withinTimeRange in
  *   the example below means that if we have to restart the child more than
  *   5 times within a 5 second window, then we should stop it.  People are
  *   frequently confused by the meanings of the retry/timing strategy
  *   parameters.</li>
  *
  * </ul>
  *
  */
class CarefulManager extends Actor with ActorLogging {

  override val supervisorStrategy =
                                  OneForOneStrategy(maxNrOfRetries = 5,
                                                    withinTimeRange = 5 seconds,
                                                    loggingEnabled = true) {

    /* NOTE: These Decider mappings are just examples, but they weren't
             chosen randomly.  IllegalStateException could be thrown because
             our actor got into some kind of addled state, but given a new
             chance at life, maybe it will have better luck next time.  An
             IllegalArgumentException suggests maybe it was told to do
             something stupid that could never work, in which case maybe it
             should be put out of its misery.  A NullPointerException might be
             because our code is just buggy, failing to check for a null where
             it should, or where it might have been smarter to use an Option
             and a pattern match or a comprehension.  Finally, if we saw a
             totally unexpected exception we could escalate it, but we should
             do this with care since we'd want some confidence the supervisor
             of this supervisor will do something intelligent, and we're
             deliberately yielding some of our desire to handle failure
             locally. */
    case _: IllegalStateException => Restart
    case _: IllegalArgumentException => Stop
    case _: NullPointerException => Stop
    case _: Exception => Escalate
  }

  override def receive: Receive = ???
}
