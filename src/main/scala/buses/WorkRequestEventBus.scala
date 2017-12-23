package buses

import akka.actor.ActorRef
import akka.event.{ ActorEventBus, LookupClassification }
import events.WorkItemRequest
import logging.Logging
import logging.Markers._

/**
 * Event bus for propagating work requests
 */
class WorkRequestEventBus(val mapSize: Int)
    extends ActorEventBus
    with LookupClassification
    with Logging {

  override type Event = WorkItemRequest
  override type Classifier = String

  override protected def classify(event: WorkItemRequest): String = {
    logger.info(requestBusMarker,s"Classifying $event")
    event.processingStage
  }

  override protected def publish(event: WorkItemRequest,
                                 subscriber: ActorRef): Unit =
    if (this.subscribers.valueIterator(event.processingStage).isEmpty) {
      logger.info(requestBusMarker,s"${event.processingStage} has no subscribers")
    } else {
      logger.info(requestBusMarker,s"routing $event to ${subscriber.path}")
      subscriber ! event
    }

}
