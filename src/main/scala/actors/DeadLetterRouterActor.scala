package actors

import akka.actor.{ DeadLetter, Props }
import buses.{ WorkRequestEventBus, WorkResponseEventBus }
import events.{ WorkItemRequest, WorkItemResponse }

/**
 * Class for re-routing dead letters in the system
 */
class DeadLetterRouterActor(workRequestEventBus: WorkRequestEventBus,
                            workResponseEventBus: WorkResponseEventBus)
    extends LoggingActor {

  def receive: Receive = {
    case deadLetter: DeadLetter =>
      log.debug(
        s"saw dead letter ${deadLetter.message}, from ${deadLetter.sender.path}"
      )
      //pop the event back on the bus
      deadLetter.message match {
        case workRequest: WorkItemRequest =>
          workRequestEventBus.publish(workRequest)
        case workResonse: WorkItemResponse =>
          workResponseEventBus.publish(workResonse)
        case _ =>
      }
  }
}

/**
 * Companion object for DeadLetterMonitorActor
 */
object DeadLetterRouterActor {

  /**
   * Create the properties object required to instantiate a DeadLetterMonitorActor
   */
  def props(workRequestEventBus: WorkRequestEventBus,
            workResponseEventBus: WorkResponseEventBus): Props =
    Props(
      new DeadLetterRouterActor(workRequestEventBus,
                                workResponseEventBus)
    )

}
