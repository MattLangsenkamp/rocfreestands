package com.rocfreestands.server.services

import cats.effect.IO
import com.rocfreestands.core.{DeleteLocationOutput, Location, Locations, LocationsService}
import com.rocfreestands.server.database.Queries.*
import com.rocfreestands.server.database.Commands.*
import skunk.data.Completion
import skunk.{Session, SqlState, Void}
import smithy4s.Timestamp

def make(lr: LocationsRepository[IO], os: ObjectStore[IO]): LocationsService[IO] =
  new LocationsService[IO]:
    override def getLocations(): IO[Locations] =
      for
        locations <- lr.getLocations
        ios <- IO.pure(locations.locations.map(l => os.getImage(l.uuid).map(img => l.copy(image = img))))
        finalLocations <- IO.parSequenceN(5)(ios)
      yield Locations(finalLocations)

    override def createLocation(
        address: String,
        name: String,
        description: String,
        latitude: Double,
        longitude: Double,
        image: String
    ): IO[Location] =
      for
        uuid             <- IO.println("yeaaa") *> IO.randomUUID.map(_.toString)
        creationDateTime <- IO.pure(Timestamp.nowUTC())
        loc <- IO.pure(
          Location(uuid, address, name, description, latitude, longitude, image, creationDateTime)
        )
        p <- os.writeImage(uuid, image)
        _ <- lr.createLocation(loc.copy(image = p.toString))
      yield loc

    override def deleteLocation(uuid: String): IO[DeleteLocationOutput] =
      for
        m <- lr.deleteLocation(uuid)
        _ <- os.deleteImage(uuid)
      yield DeleteLocationOutput("deleted")
