import actors._
import actors.DeadLetterRouterActor
import akka.actor.{ ActorSystem, DeadLetter }
import akka.routing.FromConfig
import buses.{WorkRequestEventBus, WorkResponseEventBus }
import com.typesafe.config.ConfigFactory
import events.{WorkItemRequest, WorkItemResponse }
import logging.Logging

/**
 * An actor system that implements the 'Do A Bunch Of Work Then Die' pattern
 * using a PubSub model for message propagation.
 *
 */
object Main extends Logging {

  def main(args: Array[String]): Unit = {

    val config = ConfigFactory.load()

    val numGenerators = config.getInt("app.generators.count")
    val numProcessors = config.getInt("app.processors.count")
    val numPersistors = config.getInt("app.persistors.count")
    val numWorkItems = config.getInt("app.generators.messages")

    logger.info(
      s"Started with $numGenerators generators, $numProcessors processors " +
      s"and $numPersistors persistors"
    )

    logger.info("Starting actor system")
    val system = ActorSystem("AskSystem")

    //event bus for requesting work items
    val requestEventBus = new WorkRequestEventBus(numGenerators)
    //event bus for supplying work items
    val responseEventBus = new WorkResponseEventBus(numGenerators)

    logger.info("Instantiating actors")
    val reaper =
      system.actorOf(ReaperActor.props(config), "Reaper")

    val generators = system.actorOf(
      FromConfig.props(
        GeneratorActor.props(numWorkItems, responseEventBus)
      ),
      "Generator"
    )
    val processors = system.actorOf(
      FromConfig.props(
        ProcessorActor.props(requestEventBus, responseEventBus)
      ),
      "Processor"
    )

    val persistors = system.actorOf(
      FromConfig.props(
        PersistenceActor.props(requestEventBus, responseEventBus)
      ),
      "Persistor"
    )

    val deadLetterMonitorActor =
      system.actorOf(DeadLetterRouterActor.props(requestEventBus,
                                                 responseEventBus),
                     "deadLetterMonitor")

    logger.info("Subscribing actors to events")
    system.eventStream
      .subscribe(deadLetterMonitorActor, classOf[DeadLetter])

    requestEventBus.subscribe(generators, WorkItemRequest.generate)
    requestEventBus.subscribe(processors, WorkItemRequest.process)

    responseEventBus.subscribe(processors, WorkItemResponse.generated)
    responseEventBus.subscribe(processors, WorkItemResponse.generationComplete)
    responseEventBus.subscribe(persistors, WorkItemResponse.processed)
    responseEventBus.subscribe(persistors, WorkItemResponse.processingComplete)
    responseEventBus.subscribe(reaper, WorkItemResponse.persisted)

    logger.info("Processing")
    system.whenTerminated.wait()
    logger.info("Complete")
    System.exit(0)
  }
}
