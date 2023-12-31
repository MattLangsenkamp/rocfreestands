package com.rocfreestands.server.database

import com.rocfreestands.core.Location
import skunk.Command
import skunk.codec.all.{float8, varchar}
import skunk.implicits.sql
import com.rocfreestands.server.database.Codecs.locationCodec

object Commands:

  val createLocation: Command[Location] =
    sql"INSERT INTO location VALUES ($locationCodec)".command
      .to[Location]

  val deleteLocation: Command[String] =
    sql"DELETE FROM location WHERE uuid=$varchar".command
