package buses

import akka.actor.ActorRef
import akka.event.{ ActorEventBus, LookupClassification }
import events.WorkItemResponse
import logging.Logging

/**
 * Event bus for propagating work packages
 */
class WorkResponseEventBus(val mapSize: Int)
    extends ActorEventBus
    with LookupClassification
    with Logging {

  override type Event = WorkItemResponse
  override type Classifier = String

  override protected def classify(event: WorkItemResponse): String = {
    log.debug(s"Classifying $event")
    event.processingStage
  }

  override protected def publish(event: WorkItemResponse,
                                 subscriber: ActorRef): Unit = {
    log.debug(s"routing $event to ${subscriber.path}")
    subscriber ! event
  }

}
