package com.rocfreestands.server.config
import ciris.*
import cats.effect.*
import cats.syntax.all.*
import smithy.api.NonEmptyString

object FlywayConfig:
  case class FlywayConfig(
      host: String,
      user: String,
      password: Array[Char],
      migrationsTable: String,
      migrationsLocations: List[String]
  )

  given flywayConfig: ConfigValue[IO, FlywayConfig] =
    (
      env("PSQL_HOST").as[String].default("localhost").map(h => f"jdbc:postgresql://$h/rocfreestands"),
      env("PSQL_USERNAME").as[String].default("rocfreestands"),
      env("PSQL_PASSWORD").as[String].default("password").map(_.toCharArray),
      env("MIGRATIONS_TABLE").as[String].default("flyway"),
      env("MIGRATIONS_LOCATIONS").as[String].default("db").map(_.split(",").toList)
    ).parMapN(FlywayConfig.apply)
