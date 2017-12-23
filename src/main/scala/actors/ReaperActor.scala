package actors


import akka.actor.Props
import com.typesafe.config.Config
import events.WorkItemResponse
import logging.Markers._

/**
 * Actor responsible for termination of the actor system
 */
class ReaperActor(config: Config)
    extends LoggingActor {

  //messages fully processed by the system
  private var messagesReceived = 0
  private val messagesExpectedPerGenerator =
    config.getInt("app.generators.messages")
  //this approach requires a known quantity of work
  //or at least a way to calculate that the work is 'done'

  //how many generators were started
  private val generatorsCount = config.getInt("app.generators.count")


  /**
   * Terminate the actor system
   */
  def terminateSystem(): Unit = {
    logger.info(reaperMarker,"Terminating system")
    context.system.terminate()
  }

  final def receive: Receive = {
    //keep track of completed work items
    case workItem: WorkItemResponse
        if workItem.processingStage == WorkItemResponse.persisted =>
      logger.info(reaperMarker,s"Got workItem: $workItem")
      messagesReceived += 1
      logger.info(reaperMarker,
        s"Messages processed: $messagesReceived " +
        s"(${messagesExpectedPerGenerator * generatorsCount} expected) "
      )

      //we're done, start the shutdown process
      if (messagesReceived == messagesExpectedPerGenerator * generatorsCount) {
        terminateSystem()
      }

  }

}

/**
 * Companion class
 */
object ReaperActor {

  /**
   * properties object for instantiation of a ReaperActor
   */
  def props(config: Config): Props =
    Props(new ReaperActor(config: Config))
}
