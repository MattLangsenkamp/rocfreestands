package com.rocfreestands.server.database

import com.rocfreestands.core.Location
import skunk.Codec
import skunk.codec.all.{numeric, varchar, float8}
import smithy.api.TimestampFormat.DATE_TIME
import smithy4s.Timestamp

object Codecs:

  private val timeStampCodec: Codec[Timestamp] =
    varchar(255).imap[Timestamp](s => Timestamp.parse(s, DATE_TIME).getOrElse(Timestamp.nowUTC()))(ts =>
      ts.toString
    )

  val locationCodec: Codec[Location] =
    (varchar(40) *: varchar(255) *: varchar(255) *: varchar(
      765
    ) *: float8 *: float8 *: varchar(255) *: timeStampCodec)
      .to[Location]
