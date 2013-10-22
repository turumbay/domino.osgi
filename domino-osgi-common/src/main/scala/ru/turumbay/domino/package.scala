package ru.turumbay

package object domino {
  import org.slf4j.LoggerFactory

  trait Logging {

    private[this] val logger = LoggerFactory.getLogger(this.getClass)

    def logInfo(msg: => String) = if (logger.isInfoEnabled) logger.info(msg)

    def logDebug(msg: => String) = if (logger.isDebugEnabled) logger.debug(msg)
  }
}
