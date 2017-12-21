package events

/**
 * A request for a new work package to be sent
 */
case class WorkItemRequest(processingStage: String)

/**
 * Contains values representing each of the processing stages
 */
object WorkItemRequest {
  val generate: String = "generate"
  val process: String = "process"
  val persist: String = "persist"
}
