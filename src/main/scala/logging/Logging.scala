package logging

import org.slf4j.{ Logger, LoggerFactory }

/**
 * Base trait for logging
 */
trait Logging {

  val log: Logger = LoggerFactory.getLogger(this.getClass.getSimpleName)
}
