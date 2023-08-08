package com.rocfreestands.server

import com.rocfreestands.core.{Location, Locations}
import smithy4s.{ByteArray, Timestamp}

object DB:

  var locs: Locations = Locations(
    List(
      Location(
        java.util.UUID.randomUUID.toString,
        "South",
        "South Wedge",
        "South Wedge Mission",
        43.1521 - 0.0675,
        -77.607649 - 0.0675,
        ByteArray(Array()),
        Timestamp.nowUTC()
      ),
      Location(
        java.util.UUID.randomUUID.toString,
        "north",
        "north wedge",
        "north wedge mission",
        43.1521 + 0.0675,
        -77.607649 + 0.0675,
        ByteArray(Array()),
        Timestamp.apply(2021, 1, 2)
      )
    )
  )

  def addToLocs(
      address: String,
      name: String,
      description: String,
      latitude: Double,
      longitude: Double,
      image: ByteArray
  ): Location =
    val loc = Location(
      uuid = java.util.UUID.randomUUID.toString,
      address = address,
      name = name,
      description = description,
      latitude = latitude,
      longitude = longitude,
      image = image,
      creationDateTime = Timestamp.nowUTC()
    )
    locs = Locations(locs.locations ::: loc :: Nil)
    loc

  def deleteLoc(uuid: String): Option[Location] =
    val (del, keep) = locs.locations.partition(_.uuid == uuid)
    locs = Locations(locations = keep)
    del.headOption
