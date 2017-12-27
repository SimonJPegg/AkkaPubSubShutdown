package logging

import com.typesafe.scalalogging.Logger


/**
 * Base trait for logging
 */
trait Logging {

  val logger: Logger = Logger(this.getClass.getSimpleName)
}
