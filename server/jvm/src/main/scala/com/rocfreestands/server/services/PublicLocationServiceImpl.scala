package com.rocfreestands.server.services

import cats.effect.IO
import com.rocfreestands.core.{DeleteLocationOutput, Location, Locations, PublicLocationsService}
import com.rocfreestands.server.database.Queries.*
import com.rocfreestands.server.database.Commands.*
import skunk.data.Completion
import skunk.{Session, SqlState, Void}
import smithy4s.Timestamp

object PublicLocationServiceImpl:
  def makePublicLocationService(
      lr: LocationsRepository[IO],
      os: ObjectStore[IO]
  ): PublicLocationsService[IO] =
    new PublicLocationsService[IO]:
      override def getLocations(): IO[Locations] =
        for
          _         <- IO.println("getting locations")
          locations <- lr.getLocations
          ios <- IO.pure(
            locations.locations.map(l => os.getImage(l.uuid).map(img => l.copy(image = img)))
          )
          finalLocations <- IO.parSequenceN(5)(ios)
        yield Locations(finalLocations)
