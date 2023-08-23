package com.rocfreestands.server.database

import com.rocfreestands.core.Location
import skunk.{Codec, Decoder, Query, Void, ~}
import skunk.codec.all.{numeric, varchar}
import skunk.implicits.sql
import smithy.api.TimestampFormat.DATE_TIME
import smithy4s.Timestamp
import com.rocfreestands.server.database.Codecs.locationCodec
object Queries:

  val getLocations: Query[Void, Location] =
    sql"SELECT * FROM location".query(locationCodec)

  val getLocation: Query[String, Location] =
    sql"SELECT * FROM location WHERE uuid=$varchar".query(locationCodec)
