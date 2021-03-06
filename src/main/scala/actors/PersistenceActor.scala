package actors

import akka.actor.Props
import buses.{ WorkRequestEventBus, WorkResponseEventBus }
import events.{ WorkItemRequest, WorkItemResponse }
import logging.Markers._

/**
 * Actor that mocks persisting processed work to some 'store'
 */
class PersistenceActor(requestBus: WorkRequestEventBus,
                       responseBus: WorkResponseEventBus)
    extends LoggingActor
    with ActorId {

  val id: String = idFromPath(self.path)

  logger.info(persistenceMarker,s"Persistor($id) started")

  //request our first batch of work
  val request = WorkItemRequest(WorkItemRequest.process)
  logger.info(persistenceMarker,s"Persistor($id): sending request: $request")
  requestBus.publish(request)

  override def receive: Receive = {
    //handle work items as the appear on the event bus
    case workItemResponse: WorkItemResponse
        if workItemResponse.processingStage.eq(WorkItemResponse.processed) =>
      logger.info(persistenceMarker,s"Persistor($id): received message: $workItemResponse")
      responseBus.publish(
        workItemResponse.copy(persistorId = Some(id),
                              processingStage = WorkItemResponse.persisted)
      )
      logger.info(persistenceMarker,s"Persistor($id): requesting more work")
      val request = WorkItemRequest(WorkItemRequest.process)
      requestBus.publish(request)
    //log that we're done (for a given generator)
    case workItemResponse: WorkItemResponse
        if workItemResponse.processingStage.eq(
          WorkItemResponse.processingComplete
        ) =>
      logger.info(persistenceMarker,s"Persistor($id): all processed messages have been consumed")
  }
}

/**
 * Companion class
 */
object PersistenceActor {

  /**
   * properties object for instantiation of a PersistenceActor
   */
  def props(requestBus: WorkRequestEventBus,
            responseBus: WorkResponseEventBus): Props =
    Props(new PersistenceActor(requestBus, responseBus))
}
