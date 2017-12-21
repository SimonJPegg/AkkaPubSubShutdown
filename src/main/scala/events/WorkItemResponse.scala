package events

/**
 * An item of work to be completed
 * IDs are to track progress through system
 */
case class WorkItemResponse(workItemId: Option[Long],
                            producerId: Option[String],
                            processorId: Option[String],
                            persistorId: Option[String],
                            processingStage: String)

/**
 * Contains values representing each stage a work item must
 * pass through
 */
object WorkItemResponse {
  val generated: String = "generated"
  val processed: String = "processed"
  val persisted: String = "persisted"
  val completed: String = "completed"

  //indicates that no more work is to be completed.
  val generationComplete = "generationComplete"
  val processingComplete = "processingComplete"
}
