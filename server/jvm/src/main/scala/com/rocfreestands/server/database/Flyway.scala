package com.rocfreestands.server.database

import cats.effect.{IO, Resource}
import com.rocfreestands.server.config.FlywayConfig.FlywayConfig
import fly4s.core.Fly4s
import fly4s.core.data.{
  BaselineResult,
  Fly4sConfig,
  MigrateResult,
  ValidatedMigrateResult,
  Location as MigrationLocation
}

object Flyway:
  def runFlywayMigration(dbConfig: FlywayConfig): Resource[IO, MigrateResult] = Fly4s
    .make[IO](
      url = dbConfig.url,
      user = dbConfig.user,
      password = dbConfig.password,
      config = Fly4sConfig(
        table = dbConfig.migrationsTable,
        locations = dbConfig.migrationsLocations.map(s => MigrationLocation(s))
      )
    )
    .evalTap(_.baseline)
    .evalMap(_.migrate)
