package com.rocfreestands.server

import hello.{Location, Locations}
import smithy4s.Timestamp

object DB:

  var locs: Locations = Locations(
    List(
      Location(
        1,
        "South",
        "South Wedge",
        "South Wedge Mission",
        43.1521 - 0.0675,
        -77.607649 - 0.0675,
        Timestamp.nowUTC()
      ),
      Location(
        2,
        "north",
        "north wedge",
        "north wedge mission",
        43.1521 + 0.0675,
        -77.607649 + 0.0675,
        Timestamp.apply(2021, 1, 2)
      )
    )
  )

  def addToLocs(
      address: String,
      name: String,
      description: String,
      latitude: Double,
      longitude: Double
  ): Location =
    val loc = Location(
      id = locs.locations.length + 1,
      address = address,
      name = name,
      description = description,
      latitude = latitude,
      longitude = longitude,
      creationDateTime = Timestamp.nowUTC()
    )
    locs = Locations(locs.locations ::: loc :: Nil)
    loc