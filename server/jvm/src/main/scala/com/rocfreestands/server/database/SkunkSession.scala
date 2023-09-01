package com.rocfreestands.server.database

import cats.effect._
import fs2.*
import skunk._
import cats.implicits._
import skunk.implicits._
import skunk.codec.all._
import natchez.Trace.Implicits._
object SkunkSession:

  def skunkSession: Resource[IO, Resource[IO, Session[IO]]] =
    Session.pooled[IO](
      host = "db",
      port = 5432,
      user = "rocfreestands",
      database = "rocfreestands",
      password = Some("password"),
      max = 4
    )
