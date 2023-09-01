package com.rocfreestands.server.database

import cats.effect.*
import fs2.*
import skunk.*
import cats.implicits.*
import com.rocfreestands.server.config
import com.rocfreestands.server.config.ServerConfig.ServerConfig
import skunk.implicits.*
import skunk.codec.all.*
import natchez.Trace.Implicits.*
object SkunkSession:

  def fromServerConfig(serverConfig: ServerConfig): Resource[IO, Resource[IO, Session[IO]]] =
    Session.pooled[IO](
      host = serverConfig.psqlHost,
      port = 5432,
      user = serverConfig.psqlUsername,
      database = "rocfreestands",
      password = Some(serverConfig.psqlPassword),
      max = 4
    )
