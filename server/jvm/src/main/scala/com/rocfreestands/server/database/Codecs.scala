package com.rocfreestands.server.database

import com.rocfreestands.core.Location
import com.rocfreestands.server.database.Queries.timeStampCodec
import skunk.Codec
import skunk.codec.all.{float8, varchar}
import smithy.api.TimestampFormat.DATE_TIME
import smithy4s.Timestamp

object Codecs:

  val timeStampCodec: Codec[Timestamp] =
    varchar.imap[Timestamp](s => Timestamp.parse(s, DATE_TIME).getOrElse(Timestamp.nowUTC()))(ts =>
      ts.toString
    )

  val locationCodec: Codec[Location] =
    (varchar *: varchar *: varchar *: varchar *: float8 *: float8 *: varchar *: timeStampCodec)
      .to[Location]
