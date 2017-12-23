package actors

import akka.actor.{Actor, ActorLogging}
import logging.Logging

/**
 * Actor for logging lifecycle events
 */
trait LoggingActor extends Actor with Logging {

  private val className: String = this.getClass.getSimpleName

  logger.debug(s"$className started!")

  override def preStart { logger.debug(s"$className: preStart") }

  override def postStop { logger.debug(s"$className: postStop") }

  override def preRestart(reason: Throwable, message: Option[Any]) {
    logger.debug(s"$className: preRestart")
    logger.debug(s"$className reason: ${reason.getMessage}")
    logger.debug(s"$className message: ${message.getOrElse("")}")
    super.preRestart(reason, message)
  }

  override def postRestart(reason: Throwable) {
    logger.debug(s"$className: postRestart")
    logger.debug(s"$className reason: ${reason.getMessage}")
    super.postRestart(reason)
  }

}
