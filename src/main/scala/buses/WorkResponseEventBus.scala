package buses

import akka.actor.ActorRef
import akka.event.{ ActorEventBus, LookupClassification }
import events.WorkItemResponse
import logging.Logging
import logging.Markers._

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
    logger.info(responseBusMarker,s"Classifying $event")
    event.processingStage
  }

  override protected def publish(event: WorkItemResponse,
                                 subscriber: ActorRef): Unit = {
    logger.debug(responseBusMarker,s"routing $event to ${subscriber.path}")
    subscriber ! event
  }

}
