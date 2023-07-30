package front.model

import scala.annotation.tailrec

case class LocationForm(
    name: Option[String],
    description: Option[String],
    image: Option[String],
    nameErrorMessage: Option[String],
    descriptionErrorMessage: Option[String],
    imageErrorMessage: Option[String],
)

object Location:

  private val rand = new scala.util.Random

  def newLocationForm(): LocationForm =
    LocationForm(None, None, None, None, None, None)
