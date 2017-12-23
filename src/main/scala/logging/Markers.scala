package logging

import org.slf4j.{Marker, MarkerFactory}

object Markers {

  val generationMarker : Marker = MarkerFactory.getMarker("GENERATION")
  val processingMarker : Marker = MarkerFactory.getMarker("PROCESSING")
  val persistenceMarker : Marker = MarkerFactory.getMarker("PERSISTENCE")
  val deadLetterMarker : Marker = MarkerFactory.getMarker("DEAD_LETTER")
  val reaperMarker : Marker = MarkerFactory.getMarker("REAPER")

  val requestBusMarker : Marker = MarkerFactory.getMarker("REQUEST_BUS")
  val responseBusMarker : Marker = MarkerFactory.getMarker("RESPONSE_BUS")

}
