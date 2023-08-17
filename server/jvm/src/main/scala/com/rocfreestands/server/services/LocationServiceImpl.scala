package com.rocfreestands.server.services

import cats.effect.IO
import com.rocfreestands.core.{DeleteLocationOutput, Location, Locations, LocationsService}
import com.rocfreestands.server.DB
import com.rocfreestands.server.ImageWriter

class LocationServiceImpl(db: DB, im: ImageWriter) extends LocationsService[IO] {
  override def getLocations(): IO[Locations] =
    IO.println("getting locations") *>
      IO.pure(db.locs)

  override def createLocation(
      address: String,
      name: String,
      description: String,
      latitude: Double,
      longitude: Double,
      image: String
  ): IO[Location] =
    IO.println("creating location") *>
      IO.blocking(
        db.addToLocs(address, name, description, latitude, longitude, image)
      ).flatTap(l => im.writeImage(l.uuid, l.image))

  override def deleteLocation(uuid: String): IO[DeleteLocationOutput] =
    IO.println(s"deleting location with id $uuid") *>
      IO.blocking(
        DeleteLocationOutput(
          db.deleteLoc(uuid)
            .map(_ => "Location Deleted")
            .getOrElse("Could Not Find Requested Location")
        )
      )
}
