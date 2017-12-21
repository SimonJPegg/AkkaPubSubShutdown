package actors

import akka.actor.ActorPath

/**
 * Trait for assigning an id to an actor
 */
trait ActorId {

  /**
   * The actor's id
   */
  def id: String

  /**
   * Get an actor's id from its path
   */
  def idFromPath(actorPath: ActorPath): String = {
    val actorPathString = actorPath.toString
    actorPathString.substring(actorPathString.lastIndexOf("$") + 1,
                              actorPathString.length)
  }

}
