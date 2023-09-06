package com.rocfreestands.server.services

import cats.effect.IO
import com.rocfreestands.core.{DeleteLocationOutput, Location, Locations}
import com.rocfreestands.server.database.Commands.{createLocation, deleteLocation}
import com.rocfreestands.server.database.Queries.getLocations
import skunk.data.Completion
import skunk.{Session, SqlState, Void}
import smithy4s.Timestamp

trait LocationsRepository[F[_]]:

  def getLocations: IO[Locations]

  def createLocation(location: Location): IO[Location]

  def deleteLocation(uuid: String): IO[String]
  
object LocationsRepository:  
  def makeLocationRepository(s: Session[IO]): IO[LocationsRepository[IO]] =
    for
      getLoc    <- s.prepare(getLocations)
      createLoc <- s.prepare(createLocation)
      deleteLoc <- s.prepare(deleteLocation)
    yield new LocationsRepository[IO]:
      override def getLocations: IO[Locations] =
        getLoc.stream(Void, 64).compile.toList.map(Locations.apply)
  
      override def createLocation(location: Location): IO[Location] =
        s.transaction.use { xa =>
          for
            sp               <- xa.savepoint
            creationDateTime <- IO.pure(Timestamp.nowUTC())
            _ <- createLoc
              .execute(location)
              .recoverWith { case SqlState.UniqueViolation(ex) =>
                IO.println(
                  s"Unique violation: ${ex.constraintName.getOrElse("<unknown>")}, rolling back..."
                ) *>
                  xa.rollback(sp)
              }
          yield location
        }
  
      override def deleteLocation(uuid: String): IO[String] =
        deleteLoc.execute(uuid).map(_ => "deleted location")
