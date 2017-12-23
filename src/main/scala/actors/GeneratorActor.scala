package actors

import akka.actor.Props
import buses.WorkResponseEventBus
import events.{ WorkItemRequest, WorkItemResponse }
import scala.util.control.NonFatal
import logging.Markers._

/**
 * Actor that generates work
 */
class GeneratorActor(numWorkItems: Long,
                     workResponseEventBus: WorkResponseEventBus)
    extends LoggingActor
    with ActorId {

  val id: String = idFromPath(self.path)

  //a stream of work to be processed
  private val stream: List[Long] = (1L to numWorkItems).toList

  //the current position in the stream
  private var position = 0

  logger.info(generationMarker,s"Generator($id): started with $numWorkItems items")

  override def receive: Receive = {
    //handle requests for work
    case WorkItemRequest(WorkItemRequest.generate) =>
      try {
        val workItem = WorkItemResponse(Some(stream(position)),
                                        Some(id),
                                        None,
                                        None,
                                        WorkItemResponse.generated)
        logger.info(generationMarker,s"Generator($id): published $workItem")
        //publish a new work item on the event bus
        workResponseEventBus.publish(workItem)
        position += 1
      } catch {
        case NonFatal(_) =>
          logger.info(generationMarker,s"Generator($id): stream consumed, publishing response")
          workResponseEventBus.publish(
            WorkItemResponse(None,
                             Some(id),
                             None,
                             None,
                             WorkItemResponse.generationComplete)
          )
          //required in instances where more than one generator exists
          //actors that have no more work, shouldn't be picking up
          //requests from the event bus.
          logger.info(generationMarker,s"Generator($id): removing self from pool")
          context.stop(self)
      }
  }
}

/**
 * Companion class
 */
object GeneratorActor {

  /**
   * properties object for instantiation of a GeneratorActor
   */
  def props(numWorkItems: Long,
            workResponseEventBus: WorkResponseEventBus): Props =
    Props(new GeneratorActor(numWorkItems, workResponseEventBus))
}
