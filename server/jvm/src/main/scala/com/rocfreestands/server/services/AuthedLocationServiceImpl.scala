package com.rocfreestands.server.services

import com.rocfreestands.core.DeleteLocationOutput
import cats.effect.IO
import com.rocfreestands.core.{DeleteLocationOutput, Location, Locations, PublicLocationsService}
import com.rocfreestands.core.AuthedLocationsService
import smithy4s.Timestamp

object AuthedLocationServiceImpl:
  def makeAuthedLocationService(
      lr: LocationsRepository[IO],
      os: ObjectStore[IO]
  ): AuthedLocationsService[IO] =
    new AuthedLocationsService[IO]:
      override def createLocation(
          address: String,
          name: String,
          description: String,
          latitude: Double,
          longitude: Double,
          image: String
      ): IO[Location] =
        for
          uuid             <- IO.randomUUID.map(_.toString)
          creationDateTime <- IO.pure(Timestamp.nowUTC())
          loc <- IO.pure(
            Location(uuid, address, name, description, latitude, longitude, image, creationDateTime)
          )
          _ <- IO.println(s"creating location with name: ${loc.name}")
          p <- os.writeImage(uuid, image)
          _ <- lr.createLocation(loc.copy(image = p.toString))
        yield loc

      override def deleteLocation(uuid: String): IO[DeleteLocationOutput] =
        for
          _ <- IO.println(s"deleting location with uuid: $uuid")
          m <- lr.deleteLocation(uuid)
          _ <- os.deleteImage(uuid)
        yield DeleteLocationOutput("deleted")
