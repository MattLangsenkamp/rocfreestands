package com.rocfreestands.server.database

case class FlywayConfig (url: String,
                         user: Option[String],
                         password: Option[Array[Char]],
                         migrationsTable: String,
                         migrationsLocations: List[String])
