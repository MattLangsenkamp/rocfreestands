package front.model

import scala.annotation.tailrec

case class Location(
    id: Option[Int],
    address: String,
    name: String,
    description: String,
    latitude: Double,
    longitude: Double,
    creationDateTime: Option[String]
)

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

  def randLocation(): Location =
    Location(
      Some(rand.nextInt()),
      rand.nextString(rand.nextInt(10)),
      rand.nextString(rand.nextInt(10)),
      rand.nextString(rand.nextInt(10)),
      rand.nextDouble() * .125 + 43.1521 - 0.0675,
      rand.nextDouble() * .125 + -77.607649 - 0.0675,
      Some(rand.nextString(rand.nextInt(10)))
    )

  def newLocationForm(): LocationForm =
    LocationForm(None, None, None, None, None, None)
