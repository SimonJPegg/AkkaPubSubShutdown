package actors

import akka.actor.{ Actor, ActorLogging }

/**
 * Actor for logging lifecycle events
 */
trait LoggingActor extends Actor with ActorLogging {

  private val className: String = this.getClass.getSimpleName

  log.debug(s"$className started!")

  override def preStart { log.debug(s"$className: preStart") }

  override def postStop { log.debug(s"$className: postStop") }

  override def preRestart(reason: Throwable, message: Option[Any]) {
    log.debug(s"$className: preRestart")
    log.debug(s"$className reason: ${reason.getMessage}")
    log.debug(s"$className message: ${message.getOrElse("")}")
    super.preRestart(reason, message)
  }

  override def postRestart(reason: Throwable) {
    log.debug(s"$className: postRestart")
    log.debug(s"$className reason: ${reason.getMessage}")
    super.postRestart(reason)
  }

}
