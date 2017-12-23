package actors

import akka.actor.Props
import buses.{ WorkRequestEventBus, WorkResponseEventBus }
import events.{ WorkItemRequest, WorkItemResponse }
import logging.Markers._

/**
 * Actor that simulates the processing of work items
 */
class ProcessorActor(requestBus: WorkRequestEventBus,
                     responseBus: WorkResponseEventBus)
    extends LoggingActor
    with ActorId {

  val id: String = idFromPath(self.path)

  logger.info(processingMarker,s"Processor($id) started")

  override def receive: Receive = {
    //pass any requests for more work up the chain
    case WorkItemRequest(WorkItemRequest.process) =>
      logger.info(s"Processor($id): received work request, passing up chain")
      requestBus.publish(WorkItemRequest(WorkItemRequest.generate))
    //process any work items that appear on the event bus
    case workItemResponse: WorkItemResponse
        if workItemResponse.processingStage.equals(
          WorkItemResponse.generated
        ) =>
      logger.info(processingMarker,
        s"Processor($id): recieved message $workItemResponse, processing"
      )
      responseBus.publish(
        workItemResponse.copy(processorId = Some(id),
                              processingStage = WorkItemResponse.processed)
      )
    //pass any notifications that work has been completed down the chain
    case workItemResponse: WorkItemResponse
        if workItemResponse.processingStage.equals(
          WorkItemResponse.generationComplete
        ) =>
      logger.info(processingMarker,
        s"Processor($id): all generated messages have been consumed,forwarding message"
      )
      responseBus.publish(
        workItemResponse
          .copy(processingStage = WorkItemResponse.processingComplete)
      )
  }
}

/**
 * Companion class
 */
object ProcessorActor {

  /**
   * properties object for instantiation of a ProcessorActor
   */
  def props(requestBus: WorkRequestEventBus,
            responseBus: WorkResponseEventBus): Props =
    Props(new ProcessorActor(requestBus, responseBus))
}
